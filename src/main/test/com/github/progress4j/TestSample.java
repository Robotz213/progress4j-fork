/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package com.github.progress4j;

import java.util.List;

import com.github.progress4j.imp.ProgressFactories;
import com.github.utils4j.imp.Containers;
import com.github.utils4j.imp.Threads;
import com.github.utils4j.imp.Throwables;

public class TestSample {
  enum Stage implements IStage {
    PROCESSING
  }
  
  static IProgressFactory FACTORY = ProgressFactories.THREAD;
  
  
  public static void main(String[] args) throws InterruptedException {
    
    List<Thread> requests = Containers.arrayList(
      newRequest() /*,
      newRequest(),
      newRequest(),
      newRequest() */
      //newRequest(120)
    );
  
    for(Thread r: requests) {
      r.join();
    }
    
    System.out.println("FIM");
  }


  private static Thread newRequest() {
    return newRequest(-1);
  }
  
  private static Thread newRequest(int childStep) {
    return Threads.startAsync(() -> {
      IProgressView progress = FACTORY.get();
      progress.display();
      Thread child = null;
      try {
        int total = 600;
        progress.begin(Stage.PROCESSING, total);
        for(int i = 1; i <= total; i++) {
          progress.step("Operação  " + i);
          Threads.sleep(50);
          if (i == childStep) {
            child = newRequest(childStep - 10);
          }
        }
        progress.end();
        Threads.sleep(100);
      }catch(Exception e) {
        e.printStackTrace();
      }finally {
        progress.undisplay();
        progress.dispose();
        if (child != null) {
          Throwables.tryRun(true, child::join);
        }
      }
    });
  }
}
