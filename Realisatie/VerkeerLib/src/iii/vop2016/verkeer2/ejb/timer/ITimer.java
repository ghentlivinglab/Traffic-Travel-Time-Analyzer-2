/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iii.vop2016.verkeer2.ejb.timer;

/**
 *
 * @author tobia
 */
public interface ITimer {
        void Tick();
        void StopTimer();
        boolean isTimerRunning();
        void StartTimer();
        int getCurrentInterval();
        long getCurrentTime();
}
