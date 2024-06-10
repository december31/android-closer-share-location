package com.harian.closer.share.location.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.getResourceIdOrThrow
import com.harian.closer.share.location.utils.extension.gone
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.LayoutRowProfileBinding

class BaseProfileRowView : LinearLayout {

    private var textStartRes: Int = -1
    private var textStart: String? = null
    private var textEndRes: Int = -1
    private var textEnd: String? = null
    private var iconStartRes: Int? = null
    private var iconEndRes: Int? = null

    private lateinit var binding: LayoutRowProfileBinding

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        context ?: return
        val a = if (R.styleable.ProfileRow != null && R.styleable.ProfileRow.isNotEmpty()) {
            context.theme.obtainStyledAttributes(attrs, R.styleable.ProfileRow, 0, 0)
        } else {
            return
        }

        textStartRes = a.getResourceId(R.styleable.ProfileRow_pr_textStart, -1)
        textStart = a.getString(R.styleable.ProfileRow_pr_textStart)
        textEndRes = a.getResourceId(R.styleable.ProfileRow_pr_textEnd, -1)
        textEnd = a.getString(R.styleable.ProfileRow_pr_textEnd)
        iconStartRes = try {
            a.getResourceIdOrThrow(R.styleable.ProfileRow_pr_iconStart)
        } catch (_: Exception) {
            null
        }
        iconEndRes = try {
            a.getResourceIdOrThrow(R.styleable.ProfileRow_pr_iconEnd)
        } catch (_: Exception) {
            null
        }
        a.recycle()
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)


    private fun initView() {
        binding = LayoutRowProfileBinding.inflate(LayoutInflater.from(context), this, true)
        if (textStartRes != -1) {
            binding.textStart.setText(textStartRes)
        } else if (textStart != null) {
            binding.textStart.text = textStart
        } else {
            binding.textStart.text = ""
        }
        if (textEndRes != -1) {
            binding.textEnd.setText(textEndRes)
        } else if (textEnd != null) {
            binding.textEnd.text = textEnd
        } else {
            binding.textEnd.text = ""
        }
        if (iconStartRes != null) {
            binding.iconStart.setImageResource(iconStartRes!!)
        } else {
            binding.iconStart.gone()
        }
        if (iconEndRes != null) {
            binding.iconEnd.setImageResource(iconEndRes!!)
        } else {
            binding.iconEnd.gone()
        }
    }

    fun setTextStart(@StringRes textStartRes: Int?) {
        textStartRes ?: return
        this.textStartRes = textStartRes
        binding.textStart.setText(textStartRes)
    }

    fun setTextStart(textStart: String?) {
        textStart ?: return
        this.textStart = textStart
        binding.textStart.text = textStart
    }

    fun setTextEnd(@StringRes textEndRes: Int?) {
        textEndRes ?: return
        this.textEndRes = textEndRes
        binding.textEnd.setText(textEndRes)
    }

    fun setTextEnd(textEnd: String?) {
        textEnd ?: return
        this.textEnd = textEnd
        binding.textEnd.text = textEnd
    }

    fun setIconEnd(@DrawableRes iconEndRes: Int?) {
        iconEndRes ?: return
        this.iconEndRes = iconEndRes
        binding.iconEnd.setImageResource(iconEndRes)
    }

    fun getTextStart(): String {
        return binding.textStart.text.toString()
    }

    fun getTextEnd(): String {
        return binding.textEnd.text.toString()
    }
}
