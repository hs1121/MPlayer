package com.example.mplayer.Utility

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import kotlin.reflect.KClass

/**
 * Created by Nooi on 12,February,2021
 */
@Navigator.Name("fragment")
class FragmentTransection(
    persistentClasses : Set<KClass<out Fragment>>,
    context : Context,
    manager : FragmentManager,
    containerId : Int
) : FragmentNavigator(context, manager, containerId) {

    private val navigatorHandler = PersistentNavigatorHandler(persistentClasses, manager, containerId)

    init {
        navigatorHandler.addTopFragmentIfRequired()
    }

    override fun instantiateFragment(context : Context, fragmentManager : FragmentManager, className : String, args : Bundle?) : Fragment {
        return navigatorHandler.instantiateFragment(className) {
            super.instantiateFragment(context, fragmentManager, className, args)
        }
    }
}

private class PersistentNavigatorHandler(
    private val persistentClasses : Set<KClass<out Fragment>>,
    private val manager : FragmentManager,
    private val containerId : Int
) {

    private val instances = mutableMapOf<String, Fragment>()

    fun addTopFragmentIfRequired() {
        manager.findFragmentById(containerId)
            ?.takeIf { fragment ->
                persistentClasses.any { it.qualifiedName == fragment.javaClass.name }
            }
            ?.let { fragment ->
                instances[fragment.javaClass.name] = fragment
            }
    }

    fun instantiateFragment(className : String, superCall : () -> Fragment) : Fragment {
        return instances[className] ?: superCall.invoke().also { fragment ->
            if (persistentClasses.any { it.qualifiedName == className }) {
                instances[className] = fragment
            }
        }
    }
}