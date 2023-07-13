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
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.result.contract.ActivityResultContracts
import com.anmol.essanceadmin.databinding.FragmentSpecialBinding
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class special : Fragment() {
    lateinit var binding:FragmentSpecialBinding
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
        binding= FragmentSpecialBinding.inflate(layoutInflater,container,false)
        var databaseReference:DatabaseReference=FirebaseDatabase.getInstance().getReference(arguments?.getString("class") + "/special")
        binding.getData.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if(!TextUtils.isEmpty(binding.title.text)){
                    databaseReference.child(binding.title.text.toString()).addListenerForSingleValueEvent(object :ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                                binding.title.setText(snapshot.child("title").getValue().toString())
                                binding.duration.setText(snapshot.child("duration").getValue().toString())
                                binding.size.setText(snapshot.child("size").getValue().toString())
                                binding.time.setText(snapshot.child("time").getValue().toString())
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
                if(!TextUtils.isEmpty(binding.title.text) && !TextUtils.isEmpty(binding.duration.text) && !TextUtils.isEmpty(binding.time.text) && !TextUtils.isEmpty(binding.size.text) && imgFlag){
                    databaseReference.child(binding.title.text.toString()).child("title").setValue(binding.title.text.toString())
                    databaseReference.child(binding.title.text.toString()).child("duration").setValue(binding.duration.text.toString())
                    databaseReference.child(binding.title.text.toString()).child("time").setValue(binding.time.text.toString())
                    databaseReference.child(binding.title.text.toString()).child("size").setValue(binding.size.text.toString()).addOnSuccessListener{
                        binding.title.setText("")
                        binding.duration.setText("")
                        binding.time.setText("")
                        binding.size.setText("")
                        imgFlag=false
                        Toast.makeText(requireContext(),"New Batch created successfully",LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(requireContext(),"Something went wrong",LENGTH_SHORT).show()
                }
            }
        })
        binding.update.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                if(!TextUtils.isEmpty(binding.title.text)){
                    databaseReference.child(binding.title.text.toString()).removeValue().addOnSuccessListener{
                        Toast.makeText(requireContext(),"Batch removed successfully", LENGTH_SHORT).show()
                    }
                        .addOnFailureListener{
                            Toast.makeText(requireContext(),it.message, LENGTH_SHORT).show()
                        }
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
                    var storage: StorageReference = FirebaseStorage.getInstance().getReference("special/"+binding.title.text.toString())
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
        binding.CreateBatch.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                var bundle=Bundle()
                bundle.putString("class",arguments?.getString("class"))
                var fragment=batch()
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
            special().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}