package com.example.dbwithfragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_webview.*

class WebViewFragment :Fragment() {
    private lateinit var callback: OnBackPressedCallback


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webview_pycam.setWebViewClient(object : WebViewClient() {})
        webview_pycam.loadUrl("http://toy9910.tistory.com")

        layout_img_list.setOnClickListener {
            val intent = Intent(context,PycamImageActivity::class.java)
            startActivity(intent)
        }
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                var a = activity as MainActivity
                a.replaceFragment(StartMenuFragment())
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}