
package com.su1199.thanos_soundboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.commonsware.cwac.provider.StreamProvider;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.su1199.thanos_soundboard.R;


import java.util.List;



public class MainActivity extends AppCompatActivity {



    BillingProcessor bp;
    private static final String LOG_TAG = "iabv3";

    private static final String PRODUCT_ID = "thanos_upgrade";

    private static final String LICENSE_KEY = "";
    private static final String MERCHANT_ID=null;
    private boolean readyToPurchase = false;
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;
    private SoundPlayer mSoundPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Boolean p_member = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("Pro_Member", false);

        if(!p_member){

            Bundle extras = new Bundle();
            extras.putString("max_ad_content_rating", "G");

            MobileAds.initialize(this, "");
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("");
            mInterstitialAd.loadAd(new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .build());

            MobileAds.initialize(this, "");
            mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
            .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .build();

            mAdView.loadAd(adRequest);


            bp = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
                @Override
                public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                    showToast("PRO UNLOCKED ! PLEASE RESTART YOUR APP !");
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                            .putBoolean("Pro_Member", true).commit();
                }

                @Override
                public void onBillingError(int errorCode, @Nullable Throwable error) {
                    showToast("Purchase failed");
                }

                @Override
                public void onBillingInitialized() {
                    if (bp.isPurchased(PRODUCT_ID)){
                        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                .putBoolean("Pro_Member", true).commit();
                        recreate();
                    }
                    else {
                        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                .putBoolean("Pro_Member", false).commit();
                    }

                    readyToPurchase = true;
                }

                @Override
                public void onPurchaseHistoryRestored() {

                }
            });





            mSoundPlayer = new SoundPlayer(this);
            Sound[] soundArray = SoundStore.getSounds(this);


            ListView gridView = (ListView) findViewById(R.id.gridView);


            final ArrayAdapter<Sound> adapter =
                    new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, soundArray);
            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
                    godLevel(position);
                    return true;
                }


                public void godLevel(int pos){

                    if (pos<=21) {
                        String[] dialogue = getResources().getStringArray(R.array.labels);
                        String[] path = getResources().getStringArray(R.array.provider_paths);
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_STREAM, buildUri(path[pos]));
                        sendIntent.putExtra(Intent.EXTRA_TEXT, dialogue[pos] + "\n  -The Joker" + "\n Download Thanos Sound-Board : https://play.google.com/store/apps/details?id=com.su1199.thanos_soundboard");
                        sendIntent.setType("audio/*");
                        startActivity(sendIntent);
                    }
                    else if(pos>21){
                        purchase();
                    }
                }
                private static final String AUTHORITY="com.su1199.thanos_soundboard.provider";
                private final Uri PROVIDER=Uri.parse("content://"+AUTHORITY);
                private Uri buildUri(String path) {
                    return(PROVIDER
                            .buildUpon()
                            .appendPath(StreamProvider.getUriPrefix(AUTHORITY))
                            .appendPath(path)
                            .build());
                }
            });




            gridView.setAdapter(adapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    if(position<=20) {
                        Sound sound = (Sound) parent.getItemAtPosition(position);
                        mSoundPlayer.playSound(sound);
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        } else {
                            Log.d("TAG", "The interstitial wasn't loaded yet.");
                        }
                    }
                    else if(position>20){
                        purchase();
                    }

                }
            });
        }
        else if(p_member){



            mSoundPlayer = new SoundPlayer(this);
            Sound[] soundArray = SoundStore.getSounds(this);


            ListView gridView = (ListView) findViewById(R.id.gridView);


            final ArrayAdapter<Sound> adapter =
                    new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, soundArray);
            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
                    godLevel(position);
                    return true;
                }


                public void godLevel(int pos){

                        String[] dialogue = getResources().getStringArray(R.array.labels);
                        String[] path = getResources().getStringArray(R.array.provider_paths);
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_STREAM, buildUri(path[pos]));
                        sendIntent.putExtra(Intent.EXTRA_TEXT, dialogue[pos] + "\n  -The Joker" + "\n Download Thanos Sound-Board : https://play.google.com/store/apps/details?id=com.su1199.thanos_soundboard");
                        sendIntent.setType("audio/*");
                        startActivity(sendIntent);

                }
                private static final String AUTHORITY="com.su1199.thanos_soundboard.provider";
                private final Uri PROVIDER=Uri.parse("content://"+AUTHORITY);
                private Uri buildUri(String path) {
                    return(PROVIDER
                            .buildUpon()
                            .appendPath(StreamProvider.getUriPrefix(AUTHORITY))
                            .appendPath(path)
                            .build());
                }
            });
            gridView.setAdapter(adapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {

                        Sound sound = (Sound) parent.getItemAtPosition(position);
                        mSoundPlayer.playSound(sound);

                }
            });
        }


    }

    public void purchase(){
        bp.purchase(this, "thanos_upgrade");
        showToast("Get pro to unlock all the sounds and remove ads!");
    }

    @Override
    public void onPause() {
        super.onPause();
        mSoundPlayer.release();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();
        super.onDestroy();

    }


}
