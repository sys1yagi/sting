package com.sys1yagi.sting.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.sys1yagi.sting.sample.api.KotlinSite
import kotlinx.android.synthetic.activity_main.text
import javax.inject.Inject
import kotlin.properties.Delegates

public class MainActivity : AppCompatActivity() {

    var kotlinSite: KotlinSite by Delegates.notNull()
        [Inject] set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SampleApplication.graph.inject(this)

        kotlinSite.getSite(
                {
                    text.setText(it)
                },
                {
                    text.setText(it?.getMessage())
                }
        )
    }

}
