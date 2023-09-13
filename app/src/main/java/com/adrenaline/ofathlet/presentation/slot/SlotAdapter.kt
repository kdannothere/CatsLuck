package com.adrenaline.ofathlet.presentation.slot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adrenaline.ofathlet.databinding.SlotBinding
import com.adrenaline.ofathlet.presentation.utilities.ImageUtility
import com.adrenaline.ofathlet.presentation.utilities.ViewUtility

class SlotAdapter(
    private val slotList: List<Slot>,
    private val isGame2: Boolean = false,
) :
    RecyclerView.Adapter<SlotAdapter.SlotViewHolder>() {

    class SlotViewHolder(val binding: SlotBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val slotBinding = SlotBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return SlotViewHolder(slotBinding)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val imageId = slotList[position].imageId
        holder.binding.image.apply {
            setImageResource(ImageUtility.getDrawableId(imageId, isGame2))
        }
        val hmForPort = 0.85 // heightMultiplier
        val hmForLand = 0.95 // heightMultiplier
        val hm = if (ViewUtility.orientation == 0) hmForPort else hmForLand
        val heightMultiplier:Double = if (!isGame2) 0.8 else hm
        holder.binding.root.apply {
            minHeight = (ViewUtility.getSlotHeight() * heightMultiplier).toInt()
            maxHeight = (ViewUtility.getSlotHeight() * heightMultiplier).toInt()
        }
    }

    override fun getItemCount(): Int {
        return slotList.size
    }
}