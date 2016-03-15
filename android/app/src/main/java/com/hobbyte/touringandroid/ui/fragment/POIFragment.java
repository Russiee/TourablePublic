package com.hobbyte.touringandroid.ui.fragment;

import android.os.Bundle;
import android.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.tourdata.ListViewItem;
import com.hobbyte.touringandroid.ui.activity.TourActivity;
import com.hobbyte.touringandroid.ui.adapter.PoiContentAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link ListFragment} subclass.
 * Activities that contain this fragment must implement the
 * POIFragment.OnFragmentInteractionListener interface
 * to handle interaction events.
 * Use the {@link POIFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class POIFragment extends ListFragment {
    private static final String TAG = "POIFragment";

    private static final String PARAM_OBJECTID = "objectID";
    private static final String PARAM_KEYID = "keyId";

    public static int SCREEN_HEIGHT;
    public static int SCREEN_WIDTH;

    private String objectID;
    private JSONObject poiJSON;

    public POIFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param objectID the POI's objectID
     * @return A new instance of fragment POIFragment.
     */
    public static POIFragment newInstance(String objectID, String keyID) {
        POIFragment fragment = new POIFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_OBJECTID, objectID);
        args.putString(PARAM_KEYID, keyID);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Loads the JSON for a POI, then iterates through all the items in the "post" JSONArray,
     * adding them to an Adapter which handles text and images.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenDimensions();

        if (getArguments() != null) {
            objectID = getArguments().getString(PARAM_OBJECTID);
            String keyID = getArguments().getString(PARAM_KEYID);

            poiJSON = FileManager.getJSON(
                    getActivity().getApplicationContext(),
                    keyID, String.format("poi/%s", objectID)
            );
            ListViewItem[] listItems = null;

            try {
                JSONArray post = poiJSON.getJSONArray("post");
                // drop first "Head" item?

                // TODO: all of this can go in a different class
                listItems = new ListViewItem[post.length()];

                for (int i = 0; i < post.length(); ++i) {
                    JSONObject item = post.getJSONObject(i);
                    String text;
                    String url;
                    ArrayList option;
                    String solution;
                    int type;

                    switch (item.getString("type")) {
                        case "Header":
                            text = item.getString("content");
                            if(text.equals(((AppCompatActivity) getActivity()).getSupportActionBar().getTitle().toString())) {
                                text = "";
                            }
                            url = null;
                            option = null;
                            solution = null;
                            type = PoiContentAdapter.HEADER;
                            break;
                        case "body":
                            text = item.getString("content");
                            url = null;
                            option = null;
                            solution = null;
                            type = PoiContentAdapter.BODY;
                            break;
                        case "image":
                            text = item.getString("description");
                            url = item.getString("url");
                            option = null;
                            solution = null;
                            type = PoiContentAdapter.IMAGE;
                            break;
                        case "video":
                            text = item.getString("description");
                            url = item.getString("url");
                            option = null;
                            solution = null;
                            type = PoiContentAdapter.VIDEO;
                            break;
                        case "quiz":
                            text = item.getString("question");
                            url = null;
                            option = new ArrayList<>();
                            JSONArray optAr = item.getJSONArray("options");
                            for(int j = 0; j < optAr.length(); j++) {
                                option.add(optAr.getString(j));
                            }
                            solution = item.getString("solution");
                            type = PoiContentAdapter.QUIZ;
                            break;
                        default:
                            text = "";
                            url = null;
                            option = null;
                            solution = null;
                            type = PoiContentAdapter.IGNORE_ITEM_VIEW_TYPE;
                    }

                    listItems[i] = new ListViewItem(text, type, url, option, solution);
                }
            } catch (JSONException je) {
                je.printStackTrace();
            }

            if (listItems != null) {
                PoiContentAdapter adapter = new PoiContentAdapter(
                        getActivity().getApplicationContext(),
                        listItems, keyID
                );

                setListAdapter(adapter);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_poi, container, false);
    }

    private void setScreenDimensions() {
        DisplayMetrics metrics = App.context.getResources().getDisplayMetrics();
        SCREEN_WIDTH = metrics.widthPixels;
        SCREEN_HEIGHT = metrics.heightPixels;
    }
}
