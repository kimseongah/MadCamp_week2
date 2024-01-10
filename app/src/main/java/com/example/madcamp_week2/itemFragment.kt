package com.example.madcamp_week2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.os.Parcel
import android.os.Parcelable

class itemFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item, container, false)
    }

//    companion object {
//        private const val ARG_BUSKING_ITEM = "busking_item"
//
//        fun newInstance(buskingItem: Busking): itemFragment {
//            val fragment = itemFragment()
//            val args = Bundle()
//            args.putParcelable(ARG_BUSKING_ITEM, buskingItem)
//            fragment.arguments = args
//            return fragment
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val rootView = inflater.inflate(R.layout.fragment_item, container, false)
//
//        // Access the Busking item passed via arguments
//        val buskingItem = arguments?.getParcelable<Busking>(ARG_BUSKING_ITEM)
//
//        // Update UI with buskingItem details if not null
//        buskingItem?.let {
//            rootView.findViewById<TextView>(R.id.dialog_title).text = it.title
//            rootView.findViewById<TextView>(R.id.dialog_team).text = it.team
//            rootView.findViewById<TextView>(R.id.dialog_date).text = it.date
//            rootView.findViewById<TextView>(R.id.dialog_location).text = it.location
//            rootView.findViewById<TextView>(R.id.dialog_time).text = "${it.start_time}-${it.end_time}"
//            rootView.findViewById<TextView>(R.id.dialog_set_list).text = it.setlist
//        }
//
//        return rootView
//    }
}