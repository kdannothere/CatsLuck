package com.caty.lucky.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.caty.lucky.databinding.SlotsBinding
import android.view.LayoutInflater
import com.caty.lucky.managers.MngImage

class SlotsAdapter(
    private val list: List<Slots>,
    private val gameId: Int,
) :
    RecyclerView.Adapter<SlotsAdapter.SlotViewHolder>() {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val slotBinding = SlotsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return SlotViewHolder(slotBinding)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val imageId = list[position].imageId
        holder.binding.image.apply {
            setImageResource(MngImage.getDrawableById(imageId, gameId))
        }
    }

    class SlotViewHolder(val binding: SlotsBinding) :
        RecyclerView.ViewHolder(binding.root)
}