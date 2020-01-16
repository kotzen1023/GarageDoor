package com.seventhmoon.garagedoor

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.util.Patterns
import android.view.*
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.lang.Exception
import java.lang.ref.WeakReference
import java.net.Socket
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private val mTAG = MainActivity::class.java.name

    var pref: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    private val fileName = "Preference"

    var upOnOff: Boolean = false
    var stopOnOff: Boolean = false
    var downOnOff: Boolean = false
    var oldUp: Boolean = false
    var oldStop: Boolean = false
    var oldDown: Boolean = false
    private var serverIP: String = ""
    private var toastHandle: Toast? = null

    private var handler: Handler
    init {
        val outerClass = WeakReference(this)
        handler = MyHandler(outerClass)
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        pref = getSharedPreferences(fileName, Context.MODE_PRIVATE)
        serverIP = pref!!.getString("SERVER_IP", "") as String

        auth = FirebaseAuth.getInstance()

        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference

        val btnUp = findViewById<Button>(R.id.btnUp)
        val btnStop = findViewById<Button>(R.id.btnStop)
        val btnDown = findViewById<Button>(R.id.btnDown)

        val imgUp = findViewById<ImageView>(R.id.imgUp)
        val imgStop = findViewById<ImageView>(R.id.imgStop)
        val imgDown = findViewById<ImageView>(R.id.imgDown)

        imgUp!!.setOnClickListener {
            if (!upOnOff) {
                myRef.child("door").child("up").child("on").setValue(true)
                imgUp.isEnabled = false
                imgStop.isEnabled = false
                imgDown.isEnabled = false
                imgUp.setImageResource(R.drawable.up_off)
                imgStop.setImageResource(R.drawable.stop_off)
                imgDown.setImageResource(R.drawable.down_off)


                btnUp.isEnabled = false
                btnStop.isEnabled = false
                btnDown.isEnabled = false
            }
        }

        btnUp!!.setOnClickListener {

            /*if (upOnOff)
                myRef.child("relay").child("ch1").child("on").setValue(false)
            else {*/
            /*if (!upOnOff) {
                myRef.child("door").child("up").child("on").setValue(true)
                imgUp.isEnabled = false
                imgStop.isEnabled = false
                imgDown.isEnabled = false
                imgUp.setImageResource(R.drawable.up_off)
                imgStop.setImageResource(R.drawable.stop_off)
                imgDown.setImageResource(R.drawable.down_off)

                btnUp.isEnabled = false
                btnStop.isEnabled = false
                btnDown.isEnabled = false
            }*/
            val weakRef = WeakReference(this)
            val socketThread = SocketThread(weakRef)
            socketThread.start()
            socketThread.setHandler(handler)

            Thread{
                socketThread.sendMessage("up")
            }.start()
        }

        imgStop!!.setOnClickListener {
            if (!stopOnOff) {
                myRef.child("door").child("stop").child("on").setValue(true)
                imgUp.isEnabled = false
                imgStop.isEnabled = false
                imgDown.isEnabled = false
                imgUp.setImageResource(R.drawable.up_off)
                imgStop.setImageResource(R.drawable.stop_off)
                imgDown.setImageResource(R.drawable.down_off)

                btnUp.isEnabled = false
                btnStop.isEnabled = false
                btnDown.isEnabled = false
            }
        }

        btnStop!!.setOnClickListener {

            /*if (stopOnOff)
                myRef.child("relay").child("ch2").child("on").setValue(false)
            else*/
            if (!stopOnOff) {
                myRef.child("door").child("stop").child("on").setValue(true)
                imgUp.isEnabled = false
                imgStop.isEnabled = false
                imgDown.isEnabled = false
                imgUp.setImageResource(R.drawable.up_off)
                imgStop.setImageResource(R.drawable.stop_off)
                imgDown.setImageResource(R.drawable.down_off)

                btnUp.isEnabled = false
                btnStop.isEnabled = false
                btnDown.isEnabled = false
            }
        }

        imgDown!!.setOnClickListener {
            if (!downOnOff) {
                myRef.child("door").child("down").child("on").setValue(true)
                imgUp.isEnabled = false
                imgStop.isEnabled = false
                imgDown.isEnabled = false
                imgUp.setImageResource(R.drawable.up_off)
                imgStop.setImageResource(R.drawable.stop_off)
                imgDown.setImageResource(R.drawable.down_off)

                btnUp.isEnabled = false
                btnStop.isEnabled = false
                btnDown.isEnabled = false
            }
        }

        btnDown!!.setOnClickListener {

            /*if (downOnOff)
                myRef.child("relay").child("ch3").child("on").setValue(false)
            else {*/
            if (!downOnOff) {
                myRef.child("door").child("down").child("on").setValue(true)
                imgUp.isEnabled = false
                imgStop.isEnabled = false
                imgDown.isEnabled = false
                imgUp.setImageResource(R.drawable.up_off)
                imgStop.setImageResource(R.drawable.stop_off)
                imgDown.setImageResource(R.drawable.down_off)

                btnUp.isEnabled = false
                btnStop.isEnabled = false
                btnDown.isEnabled = false
            }
        }

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                upOnOff = p0.child("door").child("up").child("on").value as Boolean
                stopOnOff = p0.child("door").child("stop").child("on").value as Boolean
                downOnOff = p0.child("door").child("down").child("on").value as Boolean
                Log.e(mTAG, "upOnOff is: $upOnOff")
                Log.e(mTAG, "stopOnOff is: $stopOnOff")
                Log.e(mTAG, "downOnOff is: $downOnOff")

                if (oldUp && !upOnOff) { //true-> false
                    imgUp.isEnabled = true
                    imgStop.isEnabled = true
                    imgDown.isEnabled = true
                    imgUp.setImageResource(R.drawable.up)
                    imgStop.setImageResource(R.drawable.stop)
                    imgDown.setImageResource(R.drawable.down)

                    btnUp.isEnabled = true
                    btnStop.isEnabled = true
                    btnDown.isEnabled = true
                } else if (oldStop &&  !stopOnOff) {
                    imgUp.isEnabled = true
                    imgStop.isEnabled = true
                    imgDown.isEnabled = true
                    imgUp.setImageResource(R.drawable.up)
                    imgStop.setImageResource(R.drawable.stop)
                    imgDown.setImageResource(R.drawable.down)

                    btnUp.isEnabled = true
                    btnStop.isEnabled = true
                    btnDown.isEnabled = true
                } else if (oldDown && !downOnOff) {
                    imgUp.isEnabled = true
                    imgStop.isEnabled = true
                    imgDown.isEnabled = true
                    imgUp.setImageResource(R.drawable.up)
                    imgStop.setImageResource(R.drawable.stop)
                    imgDown.setImageResource(R.drawable.down)

                    btnUp.isEnabled = true
                    btnStop.isEnabled = true
                    btnDown.isEnabled = true
                } else {
                    Log.d(mTAG, "not true -> false")
                }


                if (upOnOff) {
                    btnUp.text = "Up (On)"
                } else {
                    btnUp.text = "Up (Off)"
                }

                if (stopOnOff) {
                    btnStop.text = "Stop (On)"
                } else {
                    btnStop.text = "Stop (Off)"
                }

                if (downOnOff) {
                    btnDown.text = "Down (On)"
                } else {
                    btnDown.text = "Down (Off)"
                }

                oldUp = upOnOff
                oldStop = stopOnOff
                oldDown = downOnOff
                //btnUp.isEnabled = true
                //btnStop.isEnabled = true
                //btnDown.isEnabled = true
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

        if(auth.currentUser == null){
            Log.e(mTAG, "Not Log in!")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }else{
            Log.e(mTAG, "Already logged in")
        }

    }

    override fun onDestroy() {
        Log.i(mTAG, "onDestroy")
        super.onDestroy()
    }

    override fun onResume() {
        Log.i(mTAG, "onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.i(mTAG, "onPause")
        super.onPause()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.wifi_ip -> {
                showIpSetting()
            }
        }


        return true
    }

    private fun showIpSetting() {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        val promptView = View.inflate(this@MainActivity, R.layout.confirm_ip_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        val textViewMsg = promptView.findViewById<TextView>(R.id.textViewDialog)
        val editTextIP = promptView.findViewById<EditText>(R.id.editTextIp)
        val btnCancel = promptView.findViewById<Button>(R.id.btnDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnDialogConfirm)


        editTextIP.setText(serverIP)
        textViewMsg.text = getString(R.string.local_ip_addr)
        btnCancel.text = getString(R.string.cancel)
        btnConfirm.text = getString(R.string.confirm)

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
        btnCancel!!.setOnClickListener {


            alertDialogBuilder.dismiss()
        }
        btnConfirm!!.setOnClickListener {


            if(Patterns.IP_ADDRESS.matcher(editTextIP.text.toString()).matches())
            {
                serverIP = editTextIP.text.toString()
                Log.e(mTAG, "serverIP = $serverIP")

                editor = pref!!.edit()
                editor!!.putString("SERVER_IP", serverIP)
                editor!!.apply()

                alertDialogBuilder.dismiss()
            } else {
                toast(getString(R.string.invalid_ip))
            }


            /*val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
            drawer.closeDrawer(GravityCompat.START)

            val logoutIntent = Intent(Constants.ACTION.ACTION_LOGOUT_ACTION)
            mContext?.sendBroadcast(logoutIntent)
            alertDialogBuilder.dismiss()*/
        }
        alertDialogBuilder.show()


    }

    private fun toast(message: String) {

        if (toastHandle != null)
            toastHandle!!.cancel()

        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)
        val group = toast.view as ViewGroup
        val textView = group.getChildAt(0) as TextView
        textView.textSize = 30.0f
        toast.show()

        toastHandle = toast
    }

    class MyHandler(private val outerClass: WeakReference<MainActivity>) : Handler() {

        override fun handleMessage(msg: Message?) {

            outerClass.get()?.toast(msg?.obj.toString())
        }
    }

    class SocketThread(private val outerClass: WeakReference<MainActivity>):Thread() {
        private val mTAG = "socket"

        private lateinit var handler: Handler
        var writer : PrintWriter? = null

        override fun run() {
            super.run()

            Log.e(mTAG,"ip address = ${outerClass.get()?.serverIP}")

            try {
                val socket = Socket(outerClass.get()?.serverIP, 10000)
                val input = socket.getInputStream()
                val reader = BufferedReader(InputStreamReader(input))
                var text: String
                val output = socket.getOutputStream()
                writer = PrintWriter(output, true)
                while (true){
                    text = reader.readLine()
                    handler.sendMessage(handler.obtainMessage(0, text))
                }

            } catch (e: Exception) {

                e.printStackTrace()
                interrupt()
            }


        }

        fun setHandler(handler: Handler) {
            this.handler = handler
        }

        fun sendMessage(string: String) {
            Log.e(mTAG, "sendMessage: $string")
            writer?.println(string)
        }

        override fun destroy() {
            Log.e(mTAG, "destroy")
        }
    }
}
