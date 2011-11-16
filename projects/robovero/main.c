/*******************************************************************************
 * @file
 * @purpose        
 * @version        0.1
 *------------------------------------------------------------------------------
 * Copyright (C) 2011 Gumstix Inc. aaa
 * All rights reserved.
 *
 * Contributer(s):
 *   Neil MacMunn   <neil@gumstix.com>
 *------------------------------------------------------------------------------
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/

#include "return.h"
#include "table.h"

#include "LPC17xx.h"
#include "lpc_types.h"
#include "lpc17xx_gpio.h"
#include "lpc17xx_pinsel.h"
#include "lpc17xx_can.h"

/*****************************************************************************

    Generic Interrupt Service Routine

*****************************************************************************/
void IntHandler(void)
{
    unsigned long int_num;
    //long int_num;

  /* Get the interrupt number */
    __asm("mrs %0, ipsr;" : "=r"(int_num) );

  /* Disable the interrupt */
    //ROM_IntDisable(int_num);

  /* Send the interrupt signal and number */
  //usprintf(data, "\r\n%d\r\n", int_num);
  //USBBufferWrite(&g_sTxBuffer, data, strlen(data));

}

/*****************************************************************************

        Hardware Initialization Routine

*****************************************************************************/

void hwInit(void)
{
    /*
     * make the LED pin an output and turn it on
     */
    GPIO_SetDir(3, (1 << 25), 1);
    GPIO_ClearValue(3, (1 << 25));
    
    /*
     * start the usb device wait until configuration completes before proceeding
     */
    USB_Init();
    USB_Connect(TRUE);
    while (!USB_Configuration);

}

void heartbeat(void)
{
    unsigned long i;

    for (i = 0; i < 1200000; i++);
    GPIO_ClearValue(3, (1 << 25));    
    for (i = 0; i < 800000; i++);
    GPIO_SetValue(3, (1 << 25));
    for (i = 0; i < 3200000; i++);
    GPIO_ClearValue(3, (1 << 25));
    for (i = 0; i < 800000; i++);
    GPIO_SetValue(3, (1 << 25));
}

extern int heartbeat_on;

int main(void)
{
    hwInit();

    /*
     * let usbuser/robovero handle the rest
     */
    while (1)
    {
        if (heartbeat_on)
            heartbeat();
    }

    return 0;
}

