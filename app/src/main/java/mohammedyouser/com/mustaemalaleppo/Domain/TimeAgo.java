package mohammedyouser.com.mustaemalaleppo.Domain;

import android.content.Context;

import mohammedyouser.com.mustaemalaleppo.R;

public class TimeAgo {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(long time, Context context) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }


        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return context.getString(R.string.time_ago_just_now);
        } else if (diff < 2 * MINUTE_MILLIS) {
            return context.getString(R.string.time_ago_miute);
        } else if (diff < 50 * MINUTE_MILLIS) {
            return context.getString(R.string.time_ago_minutes,diff / MINUTE_MILLIS);
        } else if (diff < 90 * MINUTE_MILLIS) {
            return context.getString(R.string.time_ago_hour);

        } else if (diff < 24 * HOUR_MILLIS) {
            return context.getString(R.string.time_ago_hours,diff / HOUR_MILLIS);
        } else if (diff < 48 * HOUR_MILLIS) {
            return context.getString(R.string.time_ago_yesterday);
        } else {
            return  context.getString(R.string.time_ago_days,diff / DAY_MILLIS);
        }
    }
}