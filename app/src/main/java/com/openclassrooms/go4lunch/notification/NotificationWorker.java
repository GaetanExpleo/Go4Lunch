package com.openclassrooms.go4lunch.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.repository.UserRepository;
import com.openclassrooms.go4lunch.ui.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.openclassrooms.go4lunch.repository.UserRepository.CHOSEN_RESTAURANT_ADDRESS_FIELD;
import static com.openclassrooms.go4lunch.repository.UserRepository.CHOSEN_RESTAURANT_ID_FIELD;
import static com.openclassrooms.go4lunch.repository.UserRepository.CHOSEN_RESTAURANT_NAME_FIELD;
import static com.openclassrooms.go4lunch.repository.UserRepository.USER_NAME_FIELD;
import static com.openclassrooms.go4lunch.repository.UserRepository.USER_UID_FIELD;

public class NotificationWorker extends Worker {

    private final int NOTIFICATION_ID = 7;
    private final String NOTIFICATION_TAG = "GO4LUNCH";

    private final UserRepository mUserRepository = UserRepository.getInstance();

    private final Context mContext = getApplicationContext();

    public NotificationWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {
        String userUid = mUserRepository.getCurrentUserUID();

        String notificationTitle;
        String restaurantId;
        String notificationBody = null;

        Pair<String, String> restaurantSelected = null;
        try {
            restaurantSelected = getRestaurantSelected(userUid);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        restaurantId = restaurantSelected.first;

        if (!restaurantId.isEmpty()) {
            notificationTitle = restaurantSelected.second;
            try {
                notificationBody = getWorkmateJoining(userUid, restaurantId);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            sendVisualNotification(notificationTitle, notificationBody);
        }

        return Result.success();
    }

    /**
     * @return restaurantId if a restaurant was selected and the title of notification
     */
    private Pair<String, String> getRestaurantSelected(String userUid) throws ExecutionException, InterruptedException {
        String notificationTitle = null;
        String restaurantId = null;

        if (userUid != null) {
            QuerySnapshot querySnapshot = Tasks.await(mUserRepository
                    .getUsersCollection()
                    .whereEqualTo(USER_UID_FIELD, userUid)
                    .get());

            if (!querySnapshot.isEmpty()) {
                DocumentSnapshot userDocument = querySnapshot.getDocuments().get(0);
                restaurantId = userDocument.getString(CHOSEN_RESTAURANT_ID_FIELD);
                if (!restaurantId.isEmpty()) {
                    String restaurantName = userDocument.getString(CHOSEN_RESTAURANT_NAME_FIELD);
                    String restaurantAddress = userDocument.getString(CHOSEN_RESTAURANT_ADDRESS_FIELD);
                    notificationTitle = mContext.getString(R.string.notification_title, restaurantName, restaurantAddress);
                }
            }

        }

        return new Pair<>(restaurantId, notificationTitle);
    }

    private String getWorkmateJoining(String userUid, String restaurantId) throws ExecutionException, InterruptedException {
        QuerySnapshot querySnapshot = Tasks.await(mUserRepository
                .getUsersCollection()
                .whereNotEqualTo(USER_UID_FIELD, userUid)
                .whereEqualTo(CHOSEN_RESTAURANT_ID_FIELD, restaurantId)
                .get());


        StringBuilder stringBuilder = new StringBuilder();
        if (!querySnapshot.isEmpty()) {
            List<DocumentSnapshot> documentSnapshots = querySnapshot.getDocuments();
            stringBuilder.append(mContext.getString(R.string.lunch_with));
            for (int i = 0; i < documentSnapshots.size(); i++) {
                stringBuilder.append((i == documentSnapshots.size()-1) ?
                        mContext.getString(R.string.and) : ", ");
                stringBuilder.append(documentSnapshots.get(i).getString(USER_NAME_FIELD));
            }
        } else {
            stringBuilder.append(mContext.getString(R.string.alone));
        }

        return stringBuilder.toString();
    }

    private void sendVisualNotification(String title, String body) {
        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = getApplicationContext().getString(R.string.default_notification_channel_id);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.drawable.go4lunch_logo_white_text_en)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setTimeoutAfter(600000L)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Message from Go4Lunch";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, builder.build());
    }
}
