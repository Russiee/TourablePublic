package com.hobbyte.touringandroid.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.tourdata.ListViewItem;
import com.hobbyte.touringandroid.ui.adapter.PoiContentAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private String objectID;
    private JSONObject poiJSON;

//    private OnFragmentInteractionListener mListener;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                Pattern p = Pattern.compile(FileManager.IMG_NAME);

                for (int i = 0; i < post.length(); ++i) {
                    JSONObject item = post.getJSONObject(i);
                    int type;
                    String text = "";

                    if (item.has("type")) {
                        type = PoiContentAdapter.TEXT;
                        text = item.getString("content");
                    } else if (item.has("url")) {
                        type = PoiContentAdapter.IMG;
                        Matcher m = p.matcher(item.getString("url"));

                        if (m.matches()) {
                            text = m.group(1);
                        }
                    } else {
                        type = PoiContentAdapter.IGNORE_ITEM_VIEW_TYPE;
                    }

                    listItems[i] = new ListViewItem(text, type);
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

    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
