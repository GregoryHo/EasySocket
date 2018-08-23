package com.ns.greg.easysocket

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

/**
 * @author gregho
 * @since 2018/8/23
 */
class DemoActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_demo)
    findViewById<View>(R.id.server_btn).setOnClickListener {
      startActivity(Intent(applicationContext, ServerActivity::class.java))
    }
    findViewById<View>(R.id.client_btn).setOnClickListener {
      startActivity(Intent(applicationContext, ClientActivity::class.java))
    }
  }
}