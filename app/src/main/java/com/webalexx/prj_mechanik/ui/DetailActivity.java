package com.webalexx.prj_mechanik.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import com.webalexx.prj_mechanik.R;
import com.webalexx.prj_mechanik.content.model.CatalogItem;
import com.webalexx.prj_mechanik.services.MechanicDataSource;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import com.webalexx.prj_mechanik.content.AppConstants;

public class DetailActivity extends AppCompatActivity {

    private final static String PHONE_NUMBER_URI = "tel:88422250777";
    private final static String MAIL_TO = "mailto:";
    private final static String SUBJECT_URI = "?subject=";
    private static String ID_EXTRA;
    private static String SECTION_NAME_EXTRA;
    private static String SERIALIZABLE_CATALOG_ITEM_EXTRA;
    private static String REST_SERVER_ROOT;
    private static String ZERO_STOCK;
    private static String CURRENCY;
    private static String LOADING_ERROR;

    private final CompositeSubscription subscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ID_EXTRA = AppConstants.getContext().getResources().getString(R.string.ID_EXTRA);
        SECTION_NAME_EXTRA = AppConstants.getContext().getResources().getString(R.string.SECTION_NAME_EXTRA);
        SERIALIZABLE_CATALOG_ITEM_EXTRA = AppConstants.getContext().getResources().getString(R.string.SERIALIZABLE_CATALOG_ITEM_EXTRA);
        REST_SERVER_ROOT = AppConstants.getContext().getResources().getString(R.string.rest_server_root);
        ZERO_STOCK = AppConstants.getContext().getResources().getString(R.string.ZERO_STOCK);
        CURRENCY = AppConstants.getContext().getResources().getString(R.string.CURRENCY);
        LOADING_ERROR = AppConstants.getContext().getResources().getString(R.string.LOADING_ERROR);

        ImageView imageView = (ImageView) findViewById(R.id.detail_image);
        TextView subtextView = (TextView) findViewById(R.id.article);
        TextView textView = (TextView) findViewById(R.id.caption);
        TextView quantityView = (TextView) findViewById(R.id.quantity);
        TextView priceView = (TextView) findViewById(R.id.price);
        View priceTitle = findViewById(R.id.price_title);
        View articleTitle = findViewById(R.id.article_title);

        CatalogItem catalogItem = (CatalogItem) getIntent()
                .getExtras()
                .get(SERIALIZABLE_CATALOG_ITEM_EXTRA);
        String detailUri = catalogItem.getDetailUri();
        Picasso
                .with(DetailActivity.this)
                .load(TextUtils.isEmpty(detailUri) ? null : REST_SERVER_ROOT + detailUri)
                .placeholder(R.drawable.ic_camera_alt_grey600_48dp)
                .error(R.drawable.ic_camera_alt_grey600_48dp)
                .into(imageView);

        String catalogItemName = catalogItem.getName();
        String catalogItemArticle = catalogItem.getArticle();
        String mailSubject;
        if (catalogItemArticle == null) {
            subtextView.setVisibility(View.GONE);
            articleTitle.setVisibility(View.GONE);
            mailSubject = catalogItemName;
        } else {
            subtextView.setVisibility(View.VISIBLE);
            articleTitle.setVisibility(View.VISIBLE);
            subtextView.setText(catalogItemArticle);
            mailSubject = String.format(
                    "%s. %s: %s",
                    catalogItem,
                    getResources().getString(R.string.article),
                    catalogItemArticle
            );
        }

        textView.setText(catalogItemName);
        DetailActivity.this.setTitle(catalogItemName);

        View call = findViewById(R.id.call_action);
        View mail = findViewById(R.id.mail_action);
        call.setOnClickListener(v -> dial());
        mail.setOnClickListener(v -> mail(mailSubject));

        subscription.add(MechanicDataSource.getInstance()
                        .getStockItem(catalogItem.getId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                stockItem -> {
                                    int quantity = stockItem.getQuantity();
                                    quantityView.setText(quantity == 0
                                                    ? ZERO_STOCK
                                                    : String.valueOf(quantity)
                                    );
                                    float price = stockItem.getPrice();
                                    priceView.setText(String.valueOf(price) + CURRENCY);

                                    if (price == 0f) {
                                        priceView.setVisibility(View.GONE);
                                        priceTitle.setVisibility(View.GONE);
                                    } else {
                                        priceView.setVisibility(View.VISIBLE);
                                        priceTitle.setVisibility(View.VISIBLE);
                                    }
                                },
                                throwable -> Toast
                                        .makeText(
                                                DetailActivity.this,
                                                LOADING_ERROR,
                                                Toast.LENGTH_LONG
                                        ).show())
        );
    }

    private void dial() {
        Intent intent = new Intent(
                Intent.ACTION_DIAL,
                Uri.parse(PHONE_NUMBER_URI)
        );
        startActivity(intent);
    }

    private void mail(String subject) {
        Intent intent = new Intent(
                Intent.ACTION_SENDTO,
                Uri.parse(MAIL_TO
                                + getResources().getString(R.string.mail)
                                + SUBJECT_URI
                                + subject
                )
        );

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException exception) {
            Toast
                    .makeText(
                            DetailActivity.this,
                            R.string.no_mailing_app,
                            Toast.LENGTH_SHORT
                    ).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.homeAsUp || id == R.id.home || id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }
}
