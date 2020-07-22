package com.lucidsoftworksllc.sabotcommunity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.yarolegovich.lovelydialog.LovelyCustomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class MerchListAdapter extends RecyclerView.Adapter<MerchListAdapter.MerchListHolder> {

    private List<MerchList> merch;
    private Context context;
    //Paypal intent request code to track onActivityResult method
    public static final int PAYPAL_REQUEST_CODE = 123;
    //Paypal Configuration Object
    private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID);

    public MerchListAdapter(List<MerchList> merchListRecycler, Context context) {
        this.merch = merchListRecycler;
        this.context = context;
    }

    @Override
    public MerchListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_merch, parent, false);
        return new MerchListHolder(view);
    }

    @Override
    public void onBindViewHolder(MerchListHolder holder, int position) {
        final MerchList merch_object = merch.get(position);
        final String[] merch_option = {""};

        Glide.with(context)
                .load(Constants.BASE_URL + merch_object.getImage())
                .into(holder.merch_image);

        holder.name.setText(merch_object.getName());
        holder.merch_desc.setText(merch_object.getDesc());

        String[] items = merch_object.getOptions().split(",");
        List<String> itemList = Arrays.asList(items);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, itemList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        holder.merchSpinner.setAdapter(spinnerArrayAdapter);
        holder.merchSpinner.setSelection(0);
        holder.merchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, int position, long id) {
                merch_option[0] = String.valueOf(holder.merchSpinner.getSelectedItem());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        String paymentAmount = merch_object.getPrice();
        holder.price.setText("$"+paymentAmount);
        if (merch_object.getQuantity().equals("0")){
            holder.price.setText("Sold out!");
            holder.buyBtn.setBackgroundResource(R.drawable.darker_grey_button);
        }else{
            if (!merch_object.getSale_price().equals("")){
                holder.price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                paymentAmount = merch_object.getSale_price();
                holder.sale_price.setText("$"+paymentAmount);
                holder.sale_ends.setText("Sale ends: "+merch_object.getSale_end());
                holder.sale_ends.setVisibility(View.VISIBLE);
            }else{
                holder.sale_ends.setVisibility(View.GONE);
            }
            String finalPaymentAmount = paymentAmount;
            holder.buyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!merch_option[0].equals("")){
                        LayoutInflater li = LayoutInflater.from(context);
                        View dialog_view = li.inflate(R.layout.dialog_shipment_info, null);
                        EditText fullNameBox = dialog_view.findViewById(R.id.fullNameBox);
                        EditText streetBox = dialog_view.findViewById(R.id.streetBox);
                        EditText cityBox = dialog_view.findViewById(R.id.cityBox);
                        EditText stateBox = dialog_view.findViewById(R.id.stateBox);
                        EditText aptBox = dialog_view.findViewById(R.id.aptBox);
                        EditText zipBox = dialog_view.findViewById(R.id.zipBox);
                        EditText countryBox = dialog_view.findViewById(R.id.countryBox);
                        Button shipmentSaveBtn = dialog_view.findViewById(R.id.shipmentSaveBtn);
                        ProgressBar shipmentSaveProgress = dialog_view.findViewById(R.id.shipmentSaveProgress);
                        try {
                            JSONObject getShipmentInfoJSON = new JSONObject(SharedPrefManager.getInstance(context).getShipmentInfo());
                            JSONObject shipmentInfo = getShipmentInfoJSON.getJSONObject("shipmentInfo");
                            String fullname = shipmentInfo.getString("fullname");
                            String street = shipmentInfo.getString("street");
                            String city = shipmentInfo.getString("city");
                            String state = shipmentInfo.getString("state");
                            String apt = shipmentInfo.getString("apt");
                            String zip = shipmentInfo.getString("zip");
                            String country = shipmentInfo.getString("country");

                            fullNameBox.setText(fullname);
                            streetBox.setText(street);
                            cityBox.setText(city);
                            stateBox.setText(state);
                            aptBox.setText(apt);
                            zipBox.setText(zip);
                            countryBox.setText(country);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        final LovelyCustomDialog dialog = new LovelyCustomDialog(context);
                                dialog.setView(dialog_view)
                                .setTopColorRes(R.color.green)
                                .setTitle(R.string.home_address)
                                .setIcon(R.drawable.icons8_buy_24)
                                .setListener(R.id.shipmentSaveBtn, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        shipmentSaveBtn.setVisibility(View.GONE);
                                        shipmentSaveProgress.setVisibility(View.VISIBLE);

                                        String fullname = fullNameBox.getText().toString();
                                        String street = streetBox.getText().toString();
                                        String city = cityBox.getText().toString();
                                        String state = stateBox.getText().toString();
                                        String apt = aptBox.getText().toString();
                                        String zip = zipBox.getText().toString();
                                        String country = countryBox.getText().toString();

                                        if (!fullname.equals("")&&!street.equals("")&&!city.equals("")&&!state.equals("")&&!zip.equals("")&&!country.equals("")){
                                            try {
                                                if (SharedPrefManager.getInstance(context).setShipmentInfo(fullname,street,city,state,apt,zip,country)){
                                                    String finalname = merch_object.getName()+" [option:"+merch_option[0]+"]";
                                                    getPayment(merch_object.getId(), finalname, finalPaymentAmount);
                                                    dialog.dismiss();
                                                }else{
                                                    shipmentSaveBtn.setVisibility(View.VISIBLE);
                                                    shipmentSaveProgress.setVisibility(View.GONE);
                                                }
                                            } catch (JSONException e) {
                                                shipmentSaveBtn.setVisibility(View.VISIBLE);
                                                shipmentSaveProgress.setVisibility(View.GONE);
                                                Toast.makeText(context, "Could not save shipment information! Error #2", Toast.LENGTH_SHORT).show();
                                                e.printStackTrace();
                                            }
                                        }else{
                                            shipmentSaveBtn.setVisibility(View.VISIBLE);
                                            shipmentSaveProgress.setVisibility(View.GONE);
                                            Toast.makeText(context, "Please enter all required information!", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                })
                                .show();
                    }else{
                        Toast.makeText(context, "Please select an option!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void getPayment(String merch_id, String name, String paymentAmount) {

        //Creating a paypalpayment
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(paymentAmount)), "USD", name+" [id:"+merch_id+"]",
                PayPalPayment.PAYMENT_INTENT_SALE);

        //Creating Paypal Payment activity intent
        Intent intent = new Intent(context, PaymentActivity.class);

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        ((Activity) context).startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    public int getItemCount() {
        return merch.size();
    }

    public static class MerchListHolder extends RecyclerView.ViewHolder{
        TextView name, merch_desc, sale_ends, sale_price, price;
        ImageView merch_image;
        CardView userListLayout;
        Spinner merchSpinner;
        Button buyBtn;

        public MerchListHolder(View itemView) {
            super(itemView);
            merch_image = itemView.findViewById(R.id.merch_image);
            userListLayout = itemView.findViewById(R.id.merchLayout);
            name = itemView.findViewById(R.id.name);
            merch_desc = itemView.findViewById(R.id.merch_desc);
            merchSpinner = itemView.findViewById(R.id.merchSpinner);
            buyBtn = itemView.findViewById(R.id.buyBtn);
            sale_ends = itemView.findViewById(R.id.sale_ends);
            sale_price = itemView.findViewById(R.id.sale_price);
            price = itemView.findViewById(R.id.price);
        }
    }
}
