package com.example.imagerecognition

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth

class CustomDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_layout, null)
        val alertDialogBuilder = AlertDialog.Builder(requireActivity())
            .setView(dialogView)
            .setTitle("Are you sure that you want to log out?")
            .setPositiveButton("Yes") { dialog, _ ->
                // Handle positive button click
                dialog.dismiss()
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(requireContext(), Login::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                // Handle negative button click
                dialog.dismiss()
            }

        return alertDialogBuilder.create()
    }
}