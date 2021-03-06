package com.example.asus.penabuk.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.asus.penabuk.ErrorUtils.ErrorUtils;
import com.example.asus.penabuk.Model.Address;
import com.example.asus.penabuk.Model.City;
import com.example.asus.penabuk.Model.District;
import com.example.asus.penabuk.Model.Province;
import com.example.asus.penabuk.Model.ReqCity;
import com.example.asus.penabuk.Model.ReqDistrict;
import com.example.asus.penabuk.Model.ReqProvince;
import com.example.asus.penabuk.Model.ResMessage;
import com.example.asus.penabuk.R;
import com.example.asus.penabuk.Remote.ApiUtils;
import com.example.asus.penabuk.Remote.UserService;
import com.example.asus.penabuk.SharedPreferences.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressActivity extends AppCompatActivity {

    UserService userService = ApiUtils.getUserService();
    Context context;
    SharedPrefManager sharedPrefManager;
    Spinner spinnerProvinsi;
    Spinner spinnerKota;
    Spinner spinnerKecamatan;
    ArrayAdapter<Province> spinnerProvinsiAdapter;
    ArrayAdapter<City> spinnerKotaAdapter;
    ArrayAdapter<District> spinnerKecamatanAdapter;
    List<Province> provinces;
    List<City> cities;
    List<District> districts;
    Integer district_idselected;
    Button btnSimpan;
    EditText editKodepos;
    EditText editAlamat;
    ProgressDialog progressDialog;
    Toolbar toolbarAddAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        initView();

        doGetProvinces();

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address_line = editAlamat.getText().toString();
                String zip_code = editKodepos.getText().toString();
                Integer userId = Integer.parseInt(sharedPrefManager.getSPId());
                if(validateAddAddress(zip_code, address_line)){
                    progressDialog = ProgressDialog.show(context, null, "Please Wait..", true);
                    doAddAddress(district_idselected, address_line, zip_code, userId);
                }
            }
        });
    }

    private void initView(){
        context = this;
        sharedPrefManager = new SharedPrefManager(context);
        initToolbar();
        spinnerProvinsi = (Spinner)findViewById(R.id.spinnerProvinsi);
        spinnerKota = (Spinner)findViewById(R.id.spinnerKota);
        spinnerKecamatan = (Spinner)findViewById(R.id.spinnerKecamatan);
        spinnerKota.setEnabled(false);
        spinnerKecamatan.setEnabled(false);
        btnSimpan = (Button)findViewById(R.id.btnSimpan);
        editAlamat = (EditText)findViewById(R.id.editAlamat);
        editKodepos = (EditText)findViewById(R.id.editKodepos);
    }

    private void initToolbar(){
        toolbarAddAddress = (Toolbar)findViewById(R.id.toolbarAddAddress);
        setSupportActionBar(toolbarAddAddress);
        getSupportActionBar().setTitle("Tambah Alamat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarAddAddress.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private boolean validateAddAddress(String kodepos, String alamat){
        if(kodepos == null || kodepos.trim().length() == 0){
            editKodepos.setError("Kode Pos harus diisi.");
            return false;
        }
        if(alamat == null || alamat.trim().length() == 0){
            editAlamat.setError("Alamat harus diisi.");
            return false;
        }
        return true;
    }

    private void doGetProvinces(){
        Call<ReqProvince> call = userService.getProvince();
        call.enqueue(new Callback<ReqProvince>() {
            @Override
            public void onResponse(Call<ReqProvince> call, Response<ReqProvince> response) {
                ReqProvince reqProvince = response.body();
                provinces = reqProvince.getProvinces();

                spinnerProvinsiAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, provinces);
                spinnerProvinsiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerProvinsi.setAdapter(spinnerProvinsiAdapter);

                spinnerProvinsi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        spinnerKota.setEnabled(true);
                        Province provinceselected = (Province) spinnerProvinsi.getSelectedItem();
                        doGetCities(provinceselected.getId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onFailure(Call<ReqProvince> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doGetCities(Integer id){
        Call<ReqCity> call = userService.getCity(id);
        call.enqueue(new Callback<ReqCity>() {
            @Override
            public void onResponse(Call<ReqCity> call, Response<ReqCity> response) {
                ReqCity reqCity = response.body();
                cities = reqCity.getCities();

                spinnerKotaAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, cities);
                spinnerKotaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerKota.setAdapter(spinnerKotaAdapter);

                spinnerKota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        spinnerKecamatan.setEnabled(true);
                        City cityselected = (City)spinnerKota.getSelectedItem();
                        doGetDistricts(cityselected.getId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onFailure(Call<ReqCity> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doGetDistricts(Integer id){
        Call<ReqDistrict> call = userService.getDistrict(id);
        call.enqueue(new Callback<ReqDistrict>() {
            @Override
            public void onResponse(Call<ReqDistrict> call, Response<ReqDistrict> response) {
                ReqDistrict reqDistrict = response.body();
                districts = reqDistrict.getDistricts();

                spinnerKecamatanAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, districts);
                spinnerKecamatanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerKecamatan.setAdapter(spinnerKecamatanAdapter);

                spinnerKecamatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        District districtselected = (District)spinnerKecamatan.getSelectedItem();
                        district_idselected = districtselected.getId();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onFailure(Call<ReqDistrict> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doAddAddress(Integer district_id, String address_line, String zip_code, Integer userId){
        Address address = new Address();
        address.setDistrict_id(district_id);
        address.setAddress_line(address_line);
        address.setZip_code(zip_code);
        Call<ResMessage> call = userService.addAddressRequest(address, userId);
        call.enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                if(response.isSuccessful()) {
                    ResMessage resMessage = response.body();
                    progressDialog.dismiss();
                    Toast.makeText(AddAddressActivity.this, resMessage.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    ResMessage resMessage = ErrorUtils.parseError(response);
                    Toast.makeText(AddAddressActivity.this, resMessage.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResMessage> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}
