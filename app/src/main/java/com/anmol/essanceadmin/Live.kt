package com.anmol.essanceadmin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.result.contract.ActivityResultContracts
import com.anmol.essanceadmin.databinding.FragmentLiveBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class Live : Fragment() {
    lateinit var binding: FragmentLiveBinding
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
        binding= FragmentLiveBinding.inflate(layoutInflater,container,false)
        var databaseReference:DatabaseReference=FirebaseDatabase.getInstance().getReference(arguments?.getString("class") + "/live")

        binding.getData.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                if (!TextUtils.isEmpty(binding.title.text)){
                    databaseReference.child(binding.title.text.toString()).addListenerForSingleValueEvent(object:ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            binding.subject.setText(snapshot.child("subject").getValue().toString())
                            binding.time.setText(snapshot.child("time").getValue().toString())
                            binding.vidurl.setText(snapshot.child("url").getValue().toString())
                            imgFlag=true
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })

                }
            }
        })
        binding.create.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if(!TextUtils.isEmpty(binding.title.text) && !TextUtils.isEmpty(binding.subject.text) && !TextUtils.isEmpty(binding.time.text) && !TextUtils.isEmpty(binding.vidurl.text) && imgFlag){
                    databaseReference.child(binding.title.text.toString()).child("chapter").setValue(binding.title.text.toString())
                    databaseReference.child(binding.title.text.toString()).child("subject").setValue(binding.subject.text.toString())
                    databaseReference.child(binding.title.text.toString()).child("time").setValue(binding.time.text.toString())
                    databaseReference.child(binding.title.text.toString()).child("url").setValue(binding.vidurl.text.toString()).addOnSuccessListener{
                        Toast.makeText(requireContext(),"Live Created Successfully",LENGTH_SHORT).show()
                        binding.title.setText("")
                        binding.subject.setText("")
                        binding.time.setText("")
                        binding.vidurl.setText("")
                        imgFlag=false
                    }
                        .addOnFailureListener{
                            Toast.makeText(requireContext(),it.toString(),LENGTH_SHORT).show()
                        }
                }else{
                    Toast.makeText(requireContext(),"Something went wrong", LENGTH_SHORT).show()
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
                    var storage: StorageReference = FirebaseStorage.getInstance().getReference("live/"+binding.title.text.toString())
                    if (imgUri != null) {
                        storage.putFile(imgUri).addOnSuccessListener {
                            storage.downloadUrl.addOnSuccessListener{

                                databaseReference.child(binding.title.text.toString()).child("img").setValue(it.toString())
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
                val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                changeImage.launch(pickImg)
            }
        })
        binding.update.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                databaseReference.child(binding.title.text.toString()).removeValue().addOnSuccessListener{
                    Toast.makeText(requireContext(),"Live removed successfully", LENGTH_SHORT).show()
                }
                    .addOnFailureListener{
                        Toast.makeText(requireContext(),it.message, LENGTH_SHORT).show()
                    }
            }
        })
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Live().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}