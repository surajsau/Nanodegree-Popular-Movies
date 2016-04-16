package in.surajsau.popularmovies.network;

import android.util.Log;

import rx.Subscriber;

/**
 * Created by MacboolBro on 09/04/16.
 */
public abstract class BaseSubscriber<T> extends Subscriber<T> {

    @Override
    public void onError(Throwable e) {
        Log.d("Subscriber " + getSubscriberName(), e.getMessage());
    }

    @Override
    public void onCompleted() {
        Log.d("Subscriber " + getSubscriberName(), "Subscription completed");
    }

    public abstract String getSubscriberName();

}
