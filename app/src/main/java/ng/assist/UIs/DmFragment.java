package ng.assist.UIs;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.DirectMessagesAdapter;
import ng.assist.MainActivity;
import ng.assist.R;
import ng.assist.UIs.ViewModel.Message;
import ng.assist.UIs.ViewModel.MessageConnectionModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class DmFragment extends Fragment {

    View view;
    DirectMessagesAdapter adapter;
    RecyclerView recyclerView;
    MessageConnectionModel messageConnectionModel;
    ArrayList<MessageConnectionModel> messageConnectionModelArrayList;
    ProgressBar loadingProgress;
    UnreadReadyListener unreadReadyListener;
    int unreadCount = 0;
    public DmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        unreadReadyListener = (UnreadReadyListener) context;
    }


    public interface UnreadReadyListener{
        void onUnreadReady(int count);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_dm, container, false);

        initView();
        return  view;
    }

    private void initView(){
        loadingProgress = view.findViewById(R.id.connection_loading_progress);
        recyclerView = view.findViewById(R.id.direct_messages_recyclerview);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userEmail = preferences.getString("userEmail","");
        messageConnectionModel = new MessageConnectionModel(userEmail,getContext());
        messageConnectionModel.getConnection();
        messageConnectionModel.setConnectionListener(new MessageConnectionModel.ConnectionListener() {
            @Override
            public void onConnectionReady(ArrayList<MessageConnectionModel> messageConnectionModels) {
                messageConnectionModelArrayList = messageConnectionModels;
                loadingProgress.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter = new DirectMessagesAdapter(messageConnectionModels,getContext());
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
                validateUnreadMessage();
            }
            @Override
            public void onConnectionEmpty(String message) {
                loadingProgress.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity().getWindow().setNavigationBarColor(ContextCompat.getColor(getContext(), R.color.special_activity_background));
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(),R.color.special_activity_background));
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void addUnreadAvailable(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferences.edit().putBoolean("isUnread",true).apply();
    }

    private void validateUnreadMessage(){
        for(MessageConnectionModel messageConnectionModel: messageConnectionModelArrayList){
            if(messageConnectionModel.getUnreadCount() > 0){
                unreadCount = unreadCount + messageConnectionModel.getUnreadCount();
                addUnreadAvailable();
            }
            if(unreadCount > 0){
                unreadReadyListener.onUnreadReady(unreadCount);
            }
        }
    }






}
