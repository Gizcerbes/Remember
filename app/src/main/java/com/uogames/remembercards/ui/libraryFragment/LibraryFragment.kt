package com.uogames.remembercards.ui.libraryFragment

import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentLbraryBinding
import com.uogames.remembercards.ui.editModuleFragment.EditModuleFragment
import com.uogames.remembercards.utils.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.*
import javax.inject.Inject

class LibraryFragment : DaggerFragment() {

    @Inject
    lateinit var model: LibraryViewModel

    @Inject
    lateinit var globalViewModel: GlobalViewModel

    private var _bind: FragmentLbraryBinding? = null
    private val bind get() = _bind!!

    private var observers: Job? = null

    private val textWatcher: TextWatcher = createTextWatcher()

    private val searchImages =
        listOf(R.drawable.ic_baseline_search_off_24, R.drawable.ic_baseline_search_24)
    private val cloudImages =
        listOf(R.drawable.ic_baseline_cloud_off_24, R.drawable.ic_baseline_cloud_24)

    private var imm: InputMethodManager? = null

    private var closed = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (_bind == null) _bind = FragmentLbraryBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (closed) return
        globalViewModel.shouldReset.ifTrue { model.reset() }

        model.update()

        imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        bind.tilSearch.editText?.addTextChangedListener(textWatcher)
        bind.tilSearch.editText?.setText(model.like.value)
        bind.tilSearch.editText?.setSelection(model.like.value?.length ?: 0)

        bind.btnAdd.setOnClickListener {
            DialogNewModule {
                model.createModule(it) { moduleID ->
                    navigateToEdit(moduleID)
                }
            }.show(requireActivity().supportFragmentManager, DialogNewModule.TAG)
        }
        bind.btnNetwork.setOnClickListener { model.cloud.setOpposite() }
        bind.btnSearch.setOnClickListener { model.search.setOpposite() }

        lifecycleScope.launch{
            delay(300)
            bind.recycler.adapter = LibraryAdapter(model) { navigateToEdit(it.id) }
        }

        observers = lifecycleScope.launch {
            model.search.observe(this){
                bind.searchImage.setImageResource(searchImages[if (it) 0 else 1])
                bind.clSearchBar.visibility = if (it) View.VISIBLE else View.GONE
            }
            model.cloud.observe(this){
                bind.imgNetwork.setImageResource(cloudImages[if (it) 0 else 1])
            }
            model.size.observe(this){
                bind.txtBookEmpty.visibility = if (it == 0) View.VISIBLE else View.GONE
            }
        }
    }

    private fun createTextWatcher(): TextWatcher = ShortTextWatcher {
        model.like.value = it?.toString()
    }

    private fun navigateToEdit(moduleID: Int) {
        requireActivity().findNavController(R.id.nav_host_fragment).navigate(
            R.id.editModuleFragment,
            Bundle().apply {
                putInt(EditModuleFragment.MODULE_ID, moduleID)
            },
            navOptions {
                anim {
                    enter = R.anim.from_bottom
                    exit = R.anim.hide
                    popEnter = R.anim.show
                    popExit = R.anim.to_bottom
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        observers?.cancel()
        bind.tilSearch.editText?.removeTextChangedListener(textWatcher)
        (bind.recycler.adapter as? LibraryAdapter)?.close()
        bind.recycler.adapter = null
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        imm = null
        _bind = null
        closed = true
    }
}
