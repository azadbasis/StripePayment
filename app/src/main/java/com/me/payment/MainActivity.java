package com.me.payment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.stripe.android.paymentsheet.PaymentSheetResultCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private PaymentSheet paymentSheet;

    // TODO: when you GENERATE BY API call test
    private String publishableKey = "pk_test_51Pwb72AWXIEE26U5SCdLY4apeecyXGz3VrgqUgXQwFKfVpUdE2ADxGsxSaSQsFSWy4t7JTY6qlzzS6O1tpQ1zgqE00a6wHzM3K";
    private String secretKey = "sk_test_51Pwb72AWXIEE26U5gblmNmOSYbYu67pGBukCyVjTEaeuZlL1V2v8j7NAmLKdizYsDzq8TuYzRQxlNcQ3XJ2x3Kgo00NESikHdU";
    private String ephemeralKey;
    private String customerId;
    private Button payButton;
    private String clientSecret;


    //todo: when you received it from server
//    public static final String PUBLISHABLE_KEY ="pk_test_51PqANb02cBQm1UEtLbOzdugGF7PWpngA2Hs5495mVBBmJvSPuSSBSJaLLRnUPyRXdvhlrmxZDrzewqfQpFGY9Zk700wKyB7eEg";
//    public static final String EPHEMERAL_KEY ="ek_test_YWNjdF8xUHFBTmIwMmNCUW0xVUV0LDcwblV0MFNvaWlMU1NJSUdWMExwYjNnNEpoQWN3Y2M_00xUPVCD9A";
    //  public static final String CUSTOMER_ID ="cus_Qp66h3P3MBGZ3j";
    //  public static final String CLIENT_SECRET ="pi_3PxRtp02cBQm1UEt0T4pCVjp_secret_rtT8dq4rPZv6J75FWVhq7JYmi";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        payButton = findViewById(R.id.payButton);

        PaymentConfiguration.init(this, publishableKey);


        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            onPaymentSheetResult(paymentSheetResult);
        });

        payButton.setOnClickListener(v -> {
            paymentFlow();
        });

        getCustomer();

    }

    private void onPaymentSheetResult(PaymentSheetResult paymentSheetResult) {

        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {

            Toast.makeText(this, "Payment completed ", Toast.LENGTH_SHORT).show();

            fetchPaymentIntent(clientSecret);

        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(this, "Payment canceled", Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            PaymentSheetResult.Failed failedResult = (PaymentSheetResult.Failed) paymentSheetResult;
            String errorMessage = failedResult.getError().getMessage();
            Toast.makeText(this, "Payment failed: " + errorMessage, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Unknown payment result", Toast.LENGTH_SHORT).show();
        }

    }

    // Method to fetch the PaymentIntent details including transactionId
    private void fetchPaymentIntent(String clientSecret) {
    /*    PaymentIntentRetrieveParams params = PaymentIntentRetrieveParams.builder()
                .setClientSecret(clientSecret)
                .build();*/

        Stripe stripe = new Stripe(getApplicationContext(), publishableKey);

        stripe.retrievePaymentIntent(clientSecret, new ApiResultCallback<PaymentIntent>() {
            @Override
            public void onSuccess(@NonNull PaymentIntent paymentIntent) {
                // Retrieve the transactionId (PaymentIntent ID)
                String transactionId = paymentIntent.getId();  // PaymentIntent ID (transactionId)
                long amount = paymentIntent.getAmount();       // Amount in cents (e.g., 1000 = $10.00)
                String currency = paymentIntent.getCurrency(); // Currency (e.g., "usd")
                String status = String.valueOf(paymentIntent.getStatus());     // Payment status (e.g., "succeeded")
                String confirmationMethod = String.valueOf(paymentIntent.getConfirmationMethod());     // Payment status (e.g., "succeeded")
                // String receiptUrl = paymentIntent.getCharges().getData().get(0).getReceiptUrl();  // Receipt URL

                // Optionally, log or display the information
                Log.d(TAG, "Transaction ID: " + transactionId);
                Log.d(TAG, "Amount: " + amount);
                Log.d(TAG, "Currency: " + currency);
                Log.d(TAG, "Status: " + status);
                Log.d(TAG, "ConfirmationMethod: " + confirmationMethod);
                //   Log.d(TAG, "Receipt URL: " + receiptUrl);

                Toast.makeText(MainActivity.this, "Transaction completed", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(@NonNull Exception e) {
                // Handle the error
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Error fetching transaction ID", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void paymentFlow() {
        if (clientSecret == null || customerId == null || ephemeralKey == null || paymentSheet == null) {
            return;
        }
        paymentSheet.presentWithPaymentIntent(
                clientSecret,
                new PaymentSheet.Configuration(
                        "LightCastle Partners",
                        new PaymentSheet.CustomerConfiguration(
                                customerId,
                                ephemeralKey
                        )
                )
        );

    }
  /*  private void paymentFlow() {


        PaymentSheet.GooglePayConfiguration googlePayConfiguration = new PaymentSheet.GooglePayConfiguration(
                PaymentSheet.GooglePayConfiguration.Environment.Test, // Use Environment.Production for live
                "US" // Country code
        );

        PaymentSheet.Configuration configuration = new PaymentSheet.Configuration(
                "LightCastle Partners",
                new PaymentSheet.CustomerConfiguration(
                        customerId,
                        emphericalKey
                ),
                googlePayConfiguration // Add Google Pay configuration here
        );

        runOnUiThread(() -> {
            paymentSheet.presentWithPaymentIntent(
                    clientSecret,
                    configuration
            );
        });
    }*/


    private void getCustomer() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers",
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        customerId = jsonObject.getString("id");
                        Log.d(TAG, "getCustomer: " + customerId);
                        getEphemeralKey(customerId);
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                },
                error -> {

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + secretKey);
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getEphemeralKey(String customerId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys",
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        ephemeralKey = jsonObject.getString("id");
                        Log.d(TAG, "getEphemeralKey: " + ephemeralKey);
                        getClientSecret(ephemeralKey, customerId);
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                },
                error -> {

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + secretKey);
                //  headers.put("Stripe-Version", "2024-06-20");
                headers.put("Stripe-Version", "2022-08-01");
                return headers;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getClientSecret(String ephemeralKey, String customerId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents",
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        clientSecret = jsonObject.getString("client_secret");
                        Log.d(TAG, "getClientSecret: " + clientSecret);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + secretKey);
                return headers;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerId);
                params.put("amount", "1000");
                params.put("currency", "USD");
                params.put("automatic_payment_methods[enabled]", "true");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}