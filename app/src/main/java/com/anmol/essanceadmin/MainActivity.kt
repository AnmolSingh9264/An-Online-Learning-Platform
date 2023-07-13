package com.anmol.essanceadmin

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.style.BulletSpan
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.anmol.essanceadmin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    lateinit var fragmentManager:FragmentManager
    var Class:String?="Class 5"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var dialog: Dialog = Dialog(this)
        dialog.setContentView(R.layout.classdialog)
        dialog.setCancelable(true)
        dialog.setTitle("Choose Class")
        dialog.create()
        var radioGroup1:RadioGroup=dialog.findViewById(R.id.radioGroup)
        var radioGroup2:RadioGroup=dialog.findViewById(R.id.radioGroup2)
        radioGroup1.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                var radiobutton:RadioButton=dialog.findViewById(p1)
                Class=radiobutton.text.toString()
                dialog.dismiss()
            }
        })
        radioGroup2.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                var radiobutton:RadioButton=dialog.findViewById(p1)
                Class=radiobutton.text.toString()
                dialog.dismiss()
            }
        })
        binding.button.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                dialog.show()
            }
        })
        fragmentManager=supportFragmentManager
        var bundle:Bundle= Bundle()
        bundle.putString("class",Class)
        var popular=popular()
        popular.arguments=bundle
        fragmentManager.beginTransaction().replace(R.id.Fragment_container,popular).commit()
        binding.radioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                if (p1==binding.popular.id){
                  fragment_transition(com.anmol.essanceadmin.popular())
                }else if(p1==binding.special.id){
                    fragment_transition(special())
                }else if(p1==binding.live.id){
                   fragment_transition(Live())
                }
            }
        })
    }
    fun fragment_transition(fragment:Fragment){
        var bundle:Bundle= Bundle()
        bundle.putString("class",Class)
        fragment.arguments=bundle
        fragmentManager.beginTransaction().replace(R.id.Fragment_container,fragment).commit()
    }
}