package com.harian.closer.share.location.platform

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.harian.closer.share.location.data.common.BaseViewModel
import com.harian.closer.share.location.presentation.widget.LoadingDialog
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class BaseFragment<T : ViewDataBinding> : Fragment() {

    private var toast: Toast? = null
    private var viewModel: BaseViewModel? = null
    private val loadingDialog by lazy {
        LoadingDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSystemBarBehavior()
        setupData()
        setupUI()
        setupPermission()
        handleStateChanges()
        setupListener()
        setupLoading()
    }

    protected abstract val layoutId: Int
    private var _binding: T? = null
    protected val binding: T get() = _binding!!

    protected open fun setupUI() {}
    protected open fun setupPermission() {}
    protected open fun handleStateChanges() {}
    protected open fun setupData() {}
    protected open fun setupListener() {}

    /**
     * override this function to setup the loading state
     */
    protected open fun getFragmentViewModel(): BaseViewModel? = viewModel

    private fun setupLoading() {
        this.viewModel = getFragmentViewModel()
        viewModel?.loadingState?.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)?.onEach { isShowLoading ->
            if (isShowLoading) {
                if (childFragmentManager.findFragmentByTag(LoadingDialog::class.qualifiedName) != null) {
                    loadingDialog.show(childFragmentManager, LoadingDialog::class.qualifiedName)
                }
            } else {
                if (loadingDialog.isAdded) {
                    loadingDialog.dismiss()
                }
            }
        }?.launchIn(lifecycleScope)
    }

    @SuppressLint("WrongConstant")
    protected open fun setupSystemBarBehavior() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val insets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.statusBars())
                binding.root.setPadding(0, insets.top, 0, insets.bottom)
            }
            WindowInsetsCompat.CONSUMED
        }
    }

    fun showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
        toast?.cancel()
        toast = Toast.makeText(context, message, length)
        toast?.show()
    }

    fun showToast(stringRes: Int, length: Int = Toast.LENGTH_SHORT) {
        toast?.cancel()
        toast = Toast.makeText(context, context?.getString(stringRes), length)
        toast?.show()
    }

    protected fun makeAppDetailsIntent(): Intent? {
        return context?.let {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", it.packageName, null)
            intent.data = uri
            startActivity(intent)
            return@let intent
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
