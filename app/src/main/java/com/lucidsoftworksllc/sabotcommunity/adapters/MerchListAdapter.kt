package com.lucidsoftworksllc.sabotcommunity.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.others.PayPalConfig
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import com.lucidsoftworksllc.sabotcommunity.adapters.MerchListAdapter.MerchListHolder
import com.lucidsoftworksllc.sabotcommunity.models.MerchList
import com.paypal.android.sdk.payments.PayPalConfiguration
import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentActivity
import com.yarolegovich.lovelydialog.LovelyCustomDialog
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal

class MerchListAdapter(private val merch: List<MerchList>, private val context: Context) : RecyclerView.Adapter<MerchListHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MerchListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_merch, parent, false)
        return MerchListHolder(view)
    }

    override fun onBindViewHolder(holder: MerchListHolder, position: Int) {
        val merchObject = merch[position]
        val merchOption = arrayOf("")
        Glide.with(context)
                .load(Constants.BASE_URL + merchObject.image)
                .into(holder.merchImage)
        holder.name.text = merchObject.name
        holder.merchDesc.text = merchObject.desc
        val items = merchObject.options.split(",".toRegex()).toTypedArray()
        val itemList = listOf(*items)
        val spinnerArrayAdapter = ArrayAdapter(context, R.layout.spinner_item, itemList)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // The drop down view
        holder.merchSpinner.adapter = spinnerArrayAdapter
        holder.merchSpinner.setSelection(0)
        holder.merchSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                merchOption[0] = holder.merchSpinner.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        var paymentAmount = merchObject.price
        holder.price.text = "$$paymentAmount"
        if (merchObject.quantity == "0") {
            holder.price.text = context.getString(R.string.sold_out_text)
            holder.buyBtn.setBackgroundResource(R.drawable.darker_grey_button)
        } else {
            if (merchObject.sale_price != "") {
                holder.price.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                paymentAmount = merchObject.sale_price
                holder.salePrice.text = "$$paymentAmount"
                holder.saleEnds.text = "Sale ends: " + merchObject.sale_end
                holder.saleEnds.visibility = View.VISIBLE
            } else {
                holder.saleEnds.visibility = View.GONE
            }
            val finalPaymentAmount = paymentAmount
            holder.buyBtn.setOnClickListener {
                if (merchOption[0] != "") {
                    val li = LayoutInflater.from(context)
                    val dialogView = li.inflate(R.layout.dialog_shipment_info, null)
                    val fullNameBox = dialogView.findViewById<EditText>(R.id.fullNameBox)
                    val streetBox = dialogView.findViewById<EditText>(R.id.streetBox)
                    val cityBox = dialogView.findViewById<EditText>(R.id.cityBox)
                    val stateBox = dialogView.findViewById<EditText>(R.id.stateBox)
                    val aptBox = dialogView.findViewById<EditText>(R.id.aptBox)
                    val zipBox = dialogView.findViewById<EditText>(R.id.zipBox)
                    val countryBox = dialogView.findViewById<EditText>(R.id.countryBox)
                    val shipmentSaveBtn = dialogView.findViewById<Button>(R.id.shipmentSaveBtn)
                    val shipmentSaveProgress = dialogView.findViewById<ProgressBar>(R.id.shipmentSaveProgress)
                    try {
                        val getShipmentInfoJSON = JSONObject(SharedPrefManager.getInstance(context)!!.shipmentInfo!!)
                        val shipmentInfo = getShipmentInfoJSON.getJSONObject("shipmentInfo")
                        val fullname = shipmentInfo.getString("fullname")
                        val street = shipmentInfo.getString("street")
                        val city = shipmentInfo.getString("city")
                        val state = shipmentInfo.getString("state")
                        val apt = shipmentInfo.getString("apt")
                        val zip = shipmentInfo.getString("zip")
                        val country = shipmentInfo.getString("country")
                        fullNameBox.setText(fullname)
                        streetBox.setText(street)
                        cityBox.setText(city)
                        stateBox.setText(state)
                        aptBox.setText(apt)
                        zipBox.setText(zip)
                        countryBox.setText(country)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    val dialog = LovelyCustomDialog(context)
                    dialog.setView(dialogView)
                            .setTopColorRes(R.color.green)
                            .setTitle(R.string.home_address)
                            .setIcon(R.drawable.icons8_buy_24)
                            .setListener(R.id.shipmentSaveBtn) {
                                shipmentSaveBtn.visibility = View.GONE
                                shipmentSaveProgress.visibility = View.VISIBLE
                                val fullname = fullNameBox.text.toString()
                                val street = streetBox.text.toString()
                                val city = cityBox.text.toString()
                                val state = stateBox.text.toString()
                                val apt = aptBox.text.toString()
                                val zip = zipBox.text.toString()
                                val country = countryBox.text.toString()
                                if (fullname != "" && street != "" && city != "" && state != "" && zip != "" && country != "") {
                                    try {
                                        if (SharedPrefManager.getInstance(context)!!.setShipmentInfo(fullname, street, city, state, apt, zip, country)) {
                                            val finalname = merchObject.name + " [option:" + merchOption[0] + "]"
                                            getPayment(merchObject.id, finalname, finalPaymentAmount)
                                            dialog.dismiss()
                                        } else {
                                            shipmentSaveBtn.visibility = View.VISIBLE
                                            shipmentSaveProgress.visibility = View.GONE
                                        }
                                    } catch (e: JSONException) {
                                        shipmentSaveBtn.visibility = View.VISIBLE
                                        shipmentSaveProgress.visibility = View.GONE
                                        Toast.makeText(context, "Could not save shipment information! Error #2", Toast.LENGTH_SHORT).show()
                                        e.printStackTrace()
                                    }
                                } else {
                                    shipmentSaveBtn.visibility = View.VISIBLE
                                    shipmentSaveProgress.visibility = View.GONE
                                    Toast.makeText(context, "Please enter all required information!", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .show()
                } else {
                    Toast.makeText(context, "Please select an option!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getPayment(merch_id: String, name: String, paymentAmount: String) {

        //Creating a paypalpayment
        val payment = PayPalPayment(BigDecimal(paymentAmount), "USD", "$name [id:$merch_id]",
                PayPalPayment.PAYMENT_INTENT_SALE)

        //Creating Paypal Payment activity intent
        val intent = Intent(context, PaymentActivity::class.java)

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        (context as Activity).startActivityForResult(intent, PAYPAL_REQUEST_CODE)
    }

    override fun getItemCount(): Int {
        return merch.size
    }

    class MerchListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.name)
        var merchDesc: TextView = itemView.findViewById(R.id.merch_desc)
        var saleEnds: TextView = itemView.findViewById(R.id.sale_ends)
        var salePrice: TextView = itemView.findViewById(R.id.sale_price)
        var price: TextView = itemView.findViewById(R.id.price)
        var merchImage: ImageView = itemView.findViewById(R.id.merch_image)
        var userListLayout: CardView = itemView.findViewById(R.id.merchLayout)
        var merchSpinner: Spinner = itemView.findViewById(R.id.merchSpinner)
        var buyBtn: Button = itemView.findViewById(R.id.buyBtn)

    }

    companion object {
        //Paypal intent request code to track onActivityResult method
        const val PAYPAL_REQUEST_CODE = 123

        //Paypal Configuration Object
        private val config = PayPalConfiguration() // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
                // or live (ENVIRONMENT_PRODUCTION)
                .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
                .clientId(PayPalConfig.PAYPAL_CLIENT_ID)
    }
}