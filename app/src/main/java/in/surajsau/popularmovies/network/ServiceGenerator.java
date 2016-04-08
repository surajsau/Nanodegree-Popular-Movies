package in.surajsau.popularmovies.network;

import java.io.IOException;

import in.surajsau.popularmovies.IConstants;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by MacboolBro on 08/04/16.
 */
public class ServiceGenerator {

    private static Interceptor apiKeyInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            HttpUrl url = request.url().newBuilder().addQueryParameter(IConstants.API_KEY_PARAM, IConstants.API_KEY).build();
            request = request.newBuilder().url(url).build();
            return chain.proceed(request);
        }
    };

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                                                            .addInterceptor(apiKeyInterceptor);

    private static Retrofit.Builder builder = new Retrofit.Builder()
                                                    .addConverterFactory(JacksonConverterFactory.create())
                                                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());

    public static<T> T createService (Class<T> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }

}
