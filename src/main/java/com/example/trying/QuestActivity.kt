package com.example.trying
import android.content.Context
import android.content.Intent
import android.icu.util.ValueIterator
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import org.xml.sax.InputSource
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.FileInputStream
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.coroutines.coroutineContext


var secret:String=""
var progress:Int=0
var fileName:String=""
class QuestActivity : AppCompatActivity() {

    class CurrentQuest(file:File){
        val name:String
        val n:Int
        val i:Int
        var pair=Pair("","")
        init {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(file.inputStream(), null)
            parser.next()
            while((parser.eventType!=XmlPullParser.TEXT || parser.isWhitespace()) && parser.eventType!=XmlPullParser.END_DOCUMENT){
                parser.next()
            }
            name=parser.text

            parser.next()
            while((parser.eventType!=XmlPullParser.TEXT || parser.isWhitespace()) && parser.eventType!=XmlPullParser.END_DOCUMENT){
                parser.next()
            }
            n=parser.text.toInt()
            parser.next()
            while((parser.eventType!=XmlPullParser.TEXT || parser.isWhitespace()) && parser.eventType!=XmlPullParser.END_DOCUMENT){
                parser.next()
            }
            i=parser.text.toInt()
            progress=i
            for(o in 0..n-1){
                parser.next()
                while((parser.eventType!=XmlPullParser.TEXT || parser.isWhitespace()) && parser.eventType!=XmlPullParser.END_DOCUMENT){
                    parser.next()
                }
                val s=parser.text
                parser.next()
                while((parser.eventType!=XmlPullParser.TEXT || parser.isWhitespace()) && parser.eventType!=XmlPullParser.END_DOCUMENT){
                    parser.next()
                }
                val ss=parser.text
                if(o==i){
                    pair=Pair(s,ss)

                }
            }
            secret=pair.second


        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (supportActionBar != null)
            supportActionBar?.hide()
        fileName = intent.getStringExtra("name")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quest_activity)
        val textView:TextView=findViewById(R.id.clue) as TextView
        val butt:Button=findViewById(R.id.kitas) as Button
        val file=File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"/quests/"+fileName)
        val thing=CurrentQuest(file)
        if(thing.i==thing.n){
            textView.text="Sveikiname!"
            butt.text="Restart quest"
        } else {
        textView.text=thing.pair.first
        }

        butt?.setOnClickListener(){
            if(thing.i!=thing.n) {
                val int = IntentIntegrator(this@QuestActivity)
                int.initiateScan()
            } else {
                var replace=file.readText()
                file.writeText(replace.replace("<progress>${progress}</progress>","<progress>0</progress>"))
                finish()
                startActivity(getIntent())
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if(result != null){

            if(result.contents != null){
                //textView.text = result.contents
                if(secret==result.contents.toString()){
                    val toast = Toast.makeText(applicationContext, "teisingai!", Toast.LENGTH_SHORT)
                    toast.show()
                    val file=File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"/quests/"+fileName)
                    var ssss=file.readText()
                    file.writeText(ssss.replace("<progress>${progress}</progress>","<progress>${progress+1}</progress>"))

                } else {
                    val toast = Toast.makeText(applicationContext, "neteisinga vieta!", Toast.LENGTH_SHORT)
                    toast.show()
                }
                finish()
                startActivity(getIntent())

            } else {
                val toast = Toast.makeText(applicationContext, "nepavyko", Toast.LENGTH_SHORT)
                toast.show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
