package hu.ait.shoppinglist

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        animateImageView()

        animateText()
    }

    private fun animateText() {
        var animText = AnimationUtils.loadAnimation(
            this@SplashActivity,
            R.anim.text_splash_anim
        )

        animText.setAnimationListener(
            object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    exitSplash()
                }

                override fun onAnimationStart(animation: Animation?) {
                }
            }
        )

        tvHello.startAnimation(animText)
    }

    private fun exitSplash() {
        Thread.sleep(1000)

        startActivity(
            Intent(
                this@SplashActivity,
                ScrollingActivity::class.java
            )
        )

        finish()
    }

    private fun animateImageView() {
        var animCart = AnimationUtils.loadAnimation(
            this@SplashActivity,
            R.anim.cart_splash_anim
        )

        ivCart.startAnimation(animCart)
    }
}
