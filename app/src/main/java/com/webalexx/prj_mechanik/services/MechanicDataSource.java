package com.webalexx.prj_mechanik.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.HttpResponseCache;
import android.util.Log;

import com.webalexx.prj_mechanik.R;
import com.webalexx.prj_mechanik.content.AppConstants;
import com.webalexx.prj_mechanik.content.model.CatalogItem;
import com.webalexx.prj_mechanik.content.model.Section;
import com.webalexx.prj_mechanik.content.model.StockItem;
import com.webalexx.prj_mechanik.CustomException.CustomException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Wrapper-singleton for {@link ru.mechanik_ulyanovsk.mechanik.services.MechanicDataSource.MechanicAPI}
 */
public class MechanicDataSource {

    private static final MechanicDataSource instance = new MechanicDataSource();
    private final MechanicAPI api;
    File httpCacheDirectory;
    OkHttpClient okHttpClient;

    public static MechanicDataSource getInstance() {
        return instance;
    }

    private static String REST_SERVER_ROOT;

    private MechanicDataSource() {

        REST_SERVER_ROOT = AppConstants.getContext().getResources().getString(R.string.rest_server_root);

        httpCacheDirectory = new File(AppConstants.getContext().getCacheDir(), "localCache");
        int cacheSize = 10 * 1024 * 1024; // 1 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        //TODO: Переписать клиент с обработкой ошибок https://inthecheesefactory.com/blog/retrofit-2.0/en
        //TODO: Реализовать обработчик: при успешном получении данных с сервера, необходимо перезаписывать кэщ (возможно это делается атвоматом)
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        okHttpClient = new OkHttpClient
                .Builder()
                .addNetworkInterceptor(REWRITE_RESPONSE_INTERCEPTOR)
                .addInterceptor(OFFLINE_INTERCEPTOR)
                .addInterceptor(logging)
                .cache(cache)
                .build();

        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(REST_SERVER_ROOT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        api = restAdapter.create(MechanicAPI.class);
///*работа с файлами кеша*
//        Log.d("okHttp Ответ  10 httpCacheDirectory.toString() - >", httpCacheDirectory.toString() +
//                " файл существует ->" + httpCacheDirectory.exists() +
//                " httpCacheDirectory.getAbsoluteFile() " + httpCacheDirectory.getAbsoluteFile());
//        try {
//
//            for (File s : httpCacheDirectory.listFiles()) {
//                if (s.isFile()) {
//                    Log.d("okHttp Список файлов. Файл", s.getName().toString() +
//                            " разрешена запись ->"+ String.valueOf(s.canWrite()) +
//                            " тотал спейс -> " + String.valueOf(s.getTotalSpace()) +
//                            " lastModidied->" + String.valueOf(s.lastModified()) +
//                            " Полный путь " + s.getAbsolutePath());

//                    FileReader flR = new FileReader(s.getAbsolutePath());
//                    BufferedReader in = new BufferedReader(flR);
//                    String s1;
//                    StringBuilder sb = new StringBuilder();
//                    while ((s1 = in.readLine()) != null) {
//                        sb.append(s1);
//                        sb.append("\n");
//                    }

        //Log.d("okHttp Ответ  11 sb.toString() - >", sb.toString());
//
//                } else if (s.isDirectory()) {
//                    Log.d("okHttp Список директорий", s.getName().toString());
//
//                }
//            }
//        } catch (Exception e) {
////            e.printStackTrace();
////        } catch (IOException e) {
//            e.printStackTrace();
//
//        } finally {
//        }
    }

    private final Interceptor REWRITE_RESPONSE_INTERCEPTOR = chain -> {

        Response originalResponse = chain.proceed(chain.request());
        String cacheControl = originalResponse.header("Cache-Control");

        if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                cacheControl.contains("must-revalidate") || cacheControl.contains("max-age=0")) {
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, max-age=" + 10)
                    .removeHeader("Pragma")
                    .build();
        } else {
            return originalResponse;
        }
    };

    private final Interceptor OFFLINE_INTERCEPTOR = chain -> {
        Request request = chain.request();
        //CustomException.PrintLog("okHttp OFFLINE_INTERCEPTOR chain.proceed(request) -> ", String.valueOf(chain.request().headers()));
        if (!isOnline()) {
            int maxStale = 60 * 60 * 24 * 28; // tolerate 200-days stale
            request = request.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .removeHeader("Pragma")
                    .build();
            CustomException.PrintLog("okHttp OFFLINE_INTERCEPTOR chain.proceed(request) -> ", String.valueOf(chain.proceed(request).isSuccessful()));
        }
        return chain.proceed(request);
    };

    /**
     * Is online boolean.
     *
     * @return
     */
    public static boolean isOnline() {

        Context context = AppConstants.getContext().getApplicationContext();
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

//        CustomException.PrintLog("okHttp Ответ 2 -> cm.getActiveNetworkInfo()", cm.getActiveNetworkInfo().toString());
//        CustomException.PrintLog("okHttp Ответ 3 -> cm.getActiveNetworkInfo().isConnectedOrConnecting()", String.valueOf(cm.getActiveNetworkInfo().isConnectedOrConnecting()));
//        CustomException.PrintLog("okHttp Ответ 4 -> cm.getActiveNetworkInfo().isAvailable()", String.valueOf(cm.getActiveNetworkInfo().isAvailable()));
//        CustomException.PrintLog("okHttp Ответ 5 -> cm.getActiveNetworkInfo().isConnected()", String.valueOf(cm.getActiveNetworkInfo().isConnected()));

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting() && activeNetwork.isConnected();
    }

    public Observable<List<Section>> listSections(Long id) {
        return api
                .listSections(id)
                .cache()
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<CatalogItem>> listItems(Long sectionId) {
        return api
                .listItems(sectionId)
                .cache()
                .subscribeOn(Schedulers.io());
    }

    //TODO Now is empty but rewrite to get data from a Local cache and also add, del from the list
    public Observable<List<Section>> favoritListItems(Long sectionId) {
        return api
                .listSections(sectionId)
                .cache()
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<CatalogItem>> listItems(String filter) {
        return api
                .listItems(filter)
                .cache()
                .subscribeOn(Schedulers.io());
    }

    public Observable<StockItem> getStockItem(Long itemId) {
        return api
                .getStockItem(itemId)
                .cache()
                .subscribeOn(Schedulers.io());
    }

    /**
     * API using Retrofit[
     */
    private interface MechanicAPI {
        //@Headers("Cache-Control: max-age=620000")
        //TODO Обратить внимание на этот заголовок
        //@Headers("Cache-Control: public, max-age=17200000, s-maxage=17200000 , max-stale=2419200")
        //@Headers("Cache-Control: public, max-age=50000 , max-stale=50000")
//      @Headers(Content-Type, application/json)
        @GET("/api/sections.php")
        Observable<List<Section>> listSections(@Query("id") Long id);

        @GET("/api/items.php")
        Observable<List<CatalogItem>> listItems(@Query("section_id") Long sectionId);

        @GET("/api/items.php")
        Observable<List<CatalogItem>> listItems(@Query("filter") String filter);

        @GET("/api/int/item.php")
        Observable<StockItem> getStockItem(@Query("item_id") Long itemId);
    }
}
