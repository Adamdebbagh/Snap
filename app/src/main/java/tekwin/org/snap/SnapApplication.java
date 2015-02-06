package tekwin.org.snap;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by adamdebbagh on 2/5/15.
 */
public class SnapApplication extends Application {
    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "aXZpEI7jDUhsrVxaTuNtBjeT5e4Th3a2XyUFrfYy", "i8ek1i1teoSurbN7nlahFYpHks1m39Xfu7Ac621M");

        //test
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();


    }
}
