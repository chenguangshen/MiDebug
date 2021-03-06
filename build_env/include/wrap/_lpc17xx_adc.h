/*******************************************************************************
 * @file
 * @purpose        
 * @version        0.1
 *------------------------------------------------------------------------------
 * Copyright (C) 2011 Gumstix Inc.
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

#ifndef ___LPC17XX_ADC_H__
#define ___LPC17XX_ADC_H__

#include "lpc_types.h"

int _ADC_ChannelGetStatus(uint8_t * args);
int _ADC_GlobalGetData(uint8_t * args);
int _ADC_Init(uint8_t * args);
int _ADC_ChannelCmd(uint8_t * args);
int _ADC_GlobalGetStatus(uint8_t * args);
int _ADC_EdgeStartConfig(uint8_t * args);
int _ADC_ChannelGetData(uint8_t * args);
int _ADC_DeInit(uint8_t * args);
int _ADC_BurstCmd(uint8_t * args);
int _ADC_PowerdownCmd(uint8_t * args);
int _ADC_StartCmd(uint8_t * args);
int _ADC_IntConfig(uint8_t * args);
int _ADC_CHANNEL_SELECTION_malloc(uint8_t * args);
int _ADC_START_OPT_malloc(uint8_t * args);
int _ADC_TYPE_INT_OPT_malloc(uint8_t * args);
int _ADC_START_ON_EDGE_OPT_malloc(uint8_t * args);
int _ADC_DATA_STATUS_malloc(uint8_t * args);

#endif
