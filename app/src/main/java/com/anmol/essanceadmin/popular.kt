package com.anmol.essanceadmin

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.RadioButton
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.anmol.essanceadmin.databinding.FragmentPopularBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class popular : Fragment() {
    lateinit var binding:FragmentPopularBinding
    var imgFlag=false
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPopularBinding.inflate(layoutInflater, container, false)
        var databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(arguments?.getString("class") + "/popular")
        binding.getData.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (!TextUtils.isEmpty(binding.title.text)) {
                    databaseReference.child(binding.title.text.toString().trim())
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                binding.title.setText(snapshot.child("title").getValue().toString())
                                binding.rate.setText(snapshot.child("rate").getValue().toString())
                                binding.hour.setText(snapshot.child("hours").getValue().toString())
                                imgFlag=true
                                if (snapshot.child("langauge").getValue().toString()
                                        .equals("Hindi")
                                ) {
                                    binding.hindi.isChecked = true
                                } else {
                                    binding.english.isChecked = true
                                }
                            }
                        })
                }
            }
        })
        binding.create.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (!TextUtils.isEmpty(binding.title.text) && !TextUtils.isEmpty(binding.rate.text) && !TextUtils.isEmpty(
                        binding.hour.text
                    ) && imgFlag
                ) {
                    var radiobutton: RadioButton =
                        binding.root.findViewById(binding.radioGroup2.checkedRadioButtonId)
                    databaseReference.child(binding.title.text.toString()).child("title")
                        .setValue(binding.title.text.toString().trim())
                    databaseReference.child(binding.title.text.toString()).child("rate")
                        .setValue(binding.rate.text.toString().trim())
                    databaseReference.child(binding.title.text.toString()).child("hours")
                        .setValue(binding.hour.text.toString().trim())
                    databaseReference.child(binding.title.text.toString()).child("langauge")
                        .setValue(radiobutton.text.toString())
                    clearBoxes()
                    imgFlag = false
                    clearBoxes()
                } else {
                    Toast.makeText(requireContext(), "Something went Wrong", LENGTH_SHORT).show()
                }
            }
        })
        val changeImage =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data = it.data
                    val imgUri = data?.data
                    var storage:StorageReference=FirebaseStorage.getInstance().getReference("popular/"+binding.title.text.toString())
                    if (imgUri != null) {
                        storage.putFile(imgUri).addOnSuccessListener {
                            storage.downloadUrl.addOnSuccessListener{

                                databaseReference.child(binding.title.text.toString()).child("image").setValue(it.toString())
                                    .addOnSuccessListener{
                                        Toast.makeText(requireContext(),"Image Uploaded Successfully", LENGTH_SHORT).show()
                                        imgFlag=true
                                    }
                                    .addOnFailureListener{
                                        Toast.makeText(requireContext(),it.toString(), LENGTH_SHORT).show()
                                        imgFlag=false
                                    }

                            }
                                .addOnFailureListener{
                                    Toast.makeText(requireContext(),it.toString(), LENGTH_SHORT).show()
                                }
                        }
                            .addOnFailureListener{
                                Toast.makeText(requireContext(),it.toString(), LENGTH_SHORT).show()
                            }
                    }
                }
            }
        binding.uploadImg.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
             //   val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
               // startActivityForResult(gallery, 100)
                val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                changeImage.launch(pickImg)
            }

        })
        binding.update.setOnClickListener(object:View.OnClickListener{
            override fun onClick(p0: View?) {
                 if(!TextUtils.isEmpty(binding.title.text.toString())){
                     databaseReference.child(binding.title.text.toString()).removeValue().addOnSuccessListener{
                         Toast.makeText(requireContext(),"Removed Successfully", LENGTH_SHORT).show()
                         clearBoxes()

                     }
                         .addOnFailureListener{
                             Toast.makeText(requireContext(),it.toString(), LENGTH_SHORT).show()
                         }
                 }
            }
        })
        binding.addChpater.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                var bundle=Bundle()
                bundle.putString("class",arguments?.getString("class"))
                var fragment=addChapter()
                fragment.arguments=bundle
                var fragmentManager=requireActivity().supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.Fragment_container,fragment).addToBackStack(null).commit()
            }
        })
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            popular().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    fun clearBoxes(){
        binding.title.setText("")
        binding.rate.setText("")
        binding.hour.setText("")
        binding.radioGroup2.clearCheck()
    }
}