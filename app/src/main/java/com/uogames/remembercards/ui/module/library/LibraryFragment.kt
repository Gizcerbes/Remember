package com.uogames.remembercards.ui.module.library

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.google.android.material.color.MaterialColors
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uogames.dto.global.GlobalModuleView
import com.uogames.remembercards.viewmodel.GlobalViewModel
import com.uogames.remembercards.MainActivity.Companion.navigate
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentModuleBinding
import com.uogames.remembercards.ui.dialogs.choiceCountry.ChoiceCountryDialog
import com.uogames.remembercards.ui.dialogs.choiceLanguageDialog.ChoiceLanguageDialog
import com.uogames.remembercards.ui.module.editModuleFragment.EditModuleFragment
import com.uogames.remembercards.ui.module.watch.WatchModuleFragment
import com.uogames.remembercards.ui.reportFragment.ReportFragment
import com.uogames.remembercards.utils.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

class LibraryFragment : DaggerFragment() {

    @Inject
    lateinit var model: LibraryViewModel

    @Inject
    lateinit var globalViewModel: GlobalViewModel

    private var _bind: FragmentModuleBinding? = null
    private val bind get() = _bind!!

    private var observers: Job? = null

    private val textWatcher: TextWatcher = createTextWatcher()

    private val reportCall = { gm: GlobalModuleView -> navigateToReport(gm) }
    private val selectCall = { module: Int -> navigateToEdit(module) }
    private val watchLocal = { id: Int -> navigateToWatchLocal(id) }
    private val watchGlobal = { id: UUID -> navigateToWatchGlobal(id) }

    private val searchImages = listOf(R.drawable.ic_baseline_search_off_24, R.drawable.ic_baseline_search_24)
    private val cloudImages = listOf(R.drawable.ic_baseline_cloud_off_24, R.drawable.ic_baseline_cloud_24)

    private var imm: InputMethodManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bind = FragmentModuleBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        model.update()

        imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        bind.clSearchBar.setTextSearch(model.like.value)
        bind.clSearchBar.addTextSearchWatcher(textWatcher)

        bind.btnAdd.setOnClickListener {
            DialogNewModule {
                model.createModule(it) { moduleID -> navigateToEdit(moduleID) }
            }.show(requireActivity().supportFragmentManager, DialogNewModule.TAG)
        }
        bind.btnNetwork.setOnClickListener { model.cloud.setOpposite() }
        bind.btnSearch.setOnClickListener { model.search.setOpposite() }

        bind.clSearchBar.setOnClickLanguageFirst {
            model.languageFirst.toNull().ifTrue { return@setOnClickLanguageFirst }
            ChoiceLanguageDialog(listOf(Locale.getDefault())) {
                model.languageFirst.value = it
            }.show(requireActivity().supportFragmentManager, ChoiceLanguageDialog.TAG)
        }
        bind.clSearchBar.setOnClickLanguageSecond {
            model.languageSecond.toNull().ifTrue { return@setOnClickLanguageSecond }
            ChoiceLanguageDialog(listOf(Locale.getDefault())) {
                model.languageSecond.value = it
            }.show(requireActivity().supportFragmentManager, ChoiceLanguageDialog.TAG)
        }
        bind.clSearchBar.setOnClickCountryFirst {
            model.countryFirst.toNull().ifTrue { return@setOnClickCountryFirst }
            ChoiceCountryDialog {
                model.countryFirst.value = it
            }.show(requireActivity().supportFragmentManager, ChoiceCountryDialog.TAG)
        }
        bind.clSearchBar.setOnClickCountrySecond {
            model.countrySecond.toNull().ifTrue { return@setOnClickCountrySecond }
            ChoiceCountryDialog {
                model.countrySecond.value = it
            }.show(requireActivity().supportFragmentManager, ChoiceCountryDialog.TAG)
        }
        bind.clSearchBar.selectedNewest = model.newest.value
        bind.clSearchBar.setOnSelectedNewestListener{ model.newest.value = it }

        model.addEditCall(selectCall)
        model.addReportCall(reportCall)
        model.addWatchLocalCall(watchLocal)
        model.addWatchGlobalCall(watchGlobal)

        lifecycleScope.launchWhenStarted {
            delay(250)
            bind.recycler.adapter = model.adapter
        }

        observers = lifecycleScope.launch {
            model.search.observe(this) {
                bind.searchImage.setImageResource(searchImages[if (it) 0 else 1])
                bind.clSearchBar.visibility = if (it) View.VISIBLE else View.GONE
            }
            model.cloud.observe(this) {
                bind.imgNetwork.setImageResource(cloudImages[if (it) 0 else 1])
                bind.clSearchBar.showNewest = !it
            }
            model.size.observe(this) {
                bind.txtBookEmpty.visibility = if (it == 0) View.VISIBLE else View.GONE
            }
            model.languageFirst.observe(this) {
                bind.clSearchBar.languageFirst = it.ifNull {
                    Locale.forLanguageTag(requireContext().getText(R.string.label_all).toString())
                }
            }
            model.languageSecond.observe(this) {
                bind.clSearchBar.languageSecond = it.ifNull {
                    Locale.forLanguageTag(requireContext().getText(R.string.label_all).toString())
                }
            }
            model.countryFirst.observe(this) {
                bind.clSearchBar.setFlagResourceFirst(it?.res)
            }
            model.countrySecond.observe(this) {
                bind.clSearchBar.setFlagResourceSecond(it?.res)
            }
            model.isSearching.observe(this) {
                when(it){
                    LibraryViewModel.SearchingState.SEARCHING -> {
                        bind.lpiLoadIndicator.setIndicatorColor(MaterialColors.getColor(requireContext(), R.attr.colorPrimary, Color.BLACK))
                        bind.lpiLoadIndicator.isIndeterminate = true
                        bind.lpiLoadIndicator.visibility = View.VISIBLE
                    }
                    LibraryViewModel.SearchingState.SEARCHED -> bind.lpiLoadIndicator.visibility = View.GONE
                    else -> {
                        bind.lpiLoadIndicator.setIndicatorColor(requireContext().getColor(R.color.red))
                        bind.lpiLoadIndicator.isIndeterminate = false
                        bind.lpiLoadIndicator.progress = 100
                        bind.lpiLoadIndicator.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun createTextWatcher(): TextWatcher = ShortTextWatcher {
        model.like.value = it?.toString()
    }

    private fun navigateToEdit(moduleID: Int) = navigate(
        R.id.editModuleFragment,
        bundleOf(EditModuleFragment.MODULE_ID to moduleID)
    )

    private fun navigateToReport(gp: GlobalModuleView) = navigate(
        R.id.reportFragment,
        bundleOf(
            ReportFragment.TYPE to ReportFragment.types.MODULE,
            ReportFragment.CLAIMANT to Firebase.auth.currentUser?.uid,
            ReportFragment.ACCUSED to gp.user.globalOwner,
            ReportFragment.ITEM_ID to gp.globalId
        )
    )

    private fun navigateToWatchLocal(id: Int) = navigate(
        R.id.watchModuleFragment,
        bundleOf(
            WatchModuleFragment.MODULE_TYPE to WatchModuleFragment.ModuleType.LOCAL,
            WatchModuleFragment.MODULE_ID to id
        )
    )

    private fun navigateToWatchGlobal(id: UUID) = navigate(
        R.id.watchModuleFragment,
        bundleOf(
            WatchModuleFragment.MODULE_TYPE to WatchModuleFragment.ModuleType.GLOBAL,
            WatchModuleFragment.MODULE_ID to id
        )
    )


    override fun onDestroyView() {
        super.onDestroyView()
        observers?.cancel()
        bind.clSearchBar.removeTextSearchWatcher(textWatcher)
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        model.removeEditCall(selectCall)
        model.removeReportCall(reportCall)
        model.removeWatchGlobalCall(watchGlobal)
        model.removeWatchLocalCall(watchLocal)
        bind.recycler.adapter = null
        imm = null
        _bind = null
    }
}
