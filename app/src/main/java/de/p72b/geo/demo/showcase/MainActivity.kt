package de.p72b.geo.demo.showcase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import de.p72b.geo.demo.BR
import de.p72b.geo.demo.R
import de.p72b.geo.demo.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        viewModel = getViewModel()
        lifecycle.addObserver(viewModel)
        binding.setVariable(BR.viewmodel, viewModel)
    }
}