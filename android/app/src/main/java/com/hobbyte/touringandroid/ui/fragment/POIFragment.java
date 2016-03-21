package com.hobbyte.touringandroid.ui.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.tourdata.ListViewItem;
import com.hobbyte.touringandroid.tourdata.PointOfInterest;
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
    public static final String FOOTER = "footer";

    private static final String PARAM_OBJECTID = "objectID";
    private static final String PARAM_KEYID = "keyId";
    private static final String PARAM_PREV = "prevPOI";
    private static final String PARAM_CURR = "currPOI";

    private static final String HEADER = "header";
    private static final String BODY = "body";
    private static final String IMAGE = "image";
    private static final String VIDEO = "video";
    private static final String QUIZ = "quiz";

    public static int SCREEN_HEIGHT;
    public static int SCREEN_WIDTH;

    private String keyID;
    private String objectID;
    private JSONObject poiJSON;

    private PointOfInterest previousPOI;
    private PointOfInterest currentPOI;

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
    public static POIFragment newInstance(String objectID, String keyID, PointOfInterest previousPOI, PointOfInterest currentPOI) {
        POIFragment fragment = new POIFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_OBJECTID, objectID);
        args.putString(PARAM_KEYID, keyID);
        args.putParcelable(PARAM_PREV, previousPOI);
        args.putParcelable(PARAM_CURR, currentPOI);
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
            keyID = getArguments().getString(PARAM_KEYID);
            previousPOI = getArguments().getParcelable(PARAM_PREV);
            currentPOI = getArguments().getParcelable(PARAM_CURR);

            poiJSON = FileManager.getJSON(keyID, String.format("poi/%s", objectID));

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_poi, container, false);
    }

    private void setScreenDimensions() {
        DisplayMetrics metrics = App.context.getResources().getDisplayMetrics();
        SCREEN_WIDTH = metrics.widthPixels;
        SCREEN_HEIGHT = metrics.heightPixels;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Inflate the layout for this fragment
        ListViewItem[] listItems = null;

        View view = getActivity().getLayoutInflater().inflate(R.layout.poi_footer, null);
        LinearLayout rightLayout = (LinearLayout) view.findViewById(R.id.rightLayout);
        TextView rightPOI = (TextView) view.findViewById(R.id.nextPOIFooter);
        LinearLayout leftLayout = (LinearLayout) view.findViewById(R.id.leftLayout);
        TextView leftPOI = (TextView) view.findViewById(R.id.previousPOIFooter);
        //Checks next Point of Interest, if not null sets layout to visisble and configures onClickListener
        if (currentPOI.getNextPOI() != null) {
            rightPOI.setText("Go to Next POI (" + currentPOI.getNextPOI().getTitle() + ")");
            rightLayout.setVisibility(View.VISIBLE);
            rightLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ((TourActivity) getActivity()).previousPOI = currentPOI;
                    ((TourActivity) getActivity()).loadPointOfInterest(currentPOI.getNextPOI());
                }
            });
        } else {
            rightLayout.setVisibility(View.GONE); //Hides layout if next POI is null
        }
        //Checks previous Point of Interest, if not null and the parents of the current POI and previous POI are equal, sets layout to visible and configures listener
        if (previousPOI != null && previousPOI != currentPOI && previousPOI.getParent() == currentPOI.getParent()) {
            leftPOI.setText("Go to Previous POI (" + previousPOI.getTitle() + ")");
            leftLayout.setVisibility(View.VISIBLE);
            leftLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ((TourActivity) getActivity()).loadPointOfInterest(previousPOI);
                }
            });
        } else {
            leftLayout.setVisibility(View.GONE);
        }
        //If POI is solitary, hides the toolbar as it is not needed.
        if (rightLayout.getVisibility() == View.INVISIBLE && leftLayout.getVisibility() == View.INVISIBLE) {
            view.setVisibility(View.GONE);
        }

        getListView().addFooterView(view, FOOTER, true);

        try {
            JSONArray post = poiJSON.getJSONArray("post");
            listItems = new ListViewItem[post.length()];

            for (int i = 0; i < post.length(); ++i) {
                JSONObject item = post.getJSONObject(i);
                listItems[i] = makeListViewItemForType(item);
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

    private ListViewItem makeListViewItemForType(JSONObject item) {
        String text;
        String url = null;
        ArrayList<String> options = null;
        int solution = 0;
        int type;

        try {
            switch (item.getString("type").toLowerCase()) {
                case HEADER:
                    text = item.getString("content");
                    type = PoiContentAdapter.HEADER;
                    break;
                case BODY:
                    text = item.getString("content");
                    type = PoiContentAdapter.BODY;
                    break;
                case IMAGE:
                    text = item.getString("description");
                    url = item.getString("url");
                    type = PoiContentAdapter.IMAGE;
                    break;
                case VIDEO:
                    text = item.getString("description");
                    url = item.getString("url");
                    type = PoiContentAdapter.VIDEO;
                    break;
                case QUIZ:
                    text = item.getString("question");
                    options = new ArrayList<>();
                    JSONArray optAr = item.getJSONArray("options");

                    for (int j = 0; j < optAr.length(); j++) {
                        options.add(optAr.getString(j));
                    }

                    solution = item.getInt("solution");
                    type = PoiContentAdapter.QUIZ;
                    break;
                default:
                    text = "";
                    type = PoiContentAdapter.IGNORE_ITEM_VIEW_TYPE;

                    Log.e(TAG, "Error creating listViewItemList");
                    Log.e(TAG, String.format("Type: %s, Text: %s", type, text));
            }

            return new ListViewItem(text, type, url, options, solution);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ListViewItem("", PoiContentAdapter.IGNORE_ITEM_VIEW_TYPE, null, null, 0);
    }
}
