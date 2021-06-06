package com.example.dbwithfragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.start_ui.*

class StartMenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.start_ui, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        house_arrow.setOnClickListener{
            var a = activity as MainActivity
            a.replaceFragment(DataFragment())
        }
        room_arrow.setOnClickListener{
            var a = activity as MainActivity
            a.replaceFragment(OneDataFragment())
        }
        cctv_arrow.setOnClickListener{
            var a = activity as MainActivity
            a.replaceFragment(WebViewFragment())
        }
        user_arrow.setOnClickListener{
            var a = activity as MainActivity
            a.replaceFragment(ChangeFragment())
        }
    }
}