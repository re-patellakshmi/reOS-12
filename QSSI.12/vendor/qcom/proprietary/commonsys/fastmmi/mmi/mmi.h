/*
 * Copyright (c) 2013-2017, Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */
#ifndef __MMI_H
#define __MMI_H
#include "common.h"
#include "layout.h"
#include "xmlparse.h"

extern char g_res_file[PATH_MAX];
extern unordered_map < string, module_info * >g_modules_map;
extern unordered_map < string, module_info * >g_controller_map;
extern sem_t g_data_print_sem;
extern sem_t g_result_sem;
extern bool is_factory_tab;
extern int uart_fd;
extern bool res_tab; 

#define WAKE_LOCK "/sys/power/wake_lock"
#define WAKE_UNLOCK "/sys/power/wake_unlock"

#ifdef __LP64__
#define AUDIO_TESTPIN_CTRL1    1169
#define AUDIO_TESTPIN_CTRL2    1210
#define AUDIO_TESTPIN_CTRL3    1252
#define AUDIO_TESTPIN_CTRL4    1208
#else
#define AUDIO_TESTPIN_CTRL1    913
#define AUDIO_TESTPIN_CTRL2    954
#define AUDIO_TESTPIN_CTRL3    996
#define AUDIO_TESTPIN_CTRL4    952
#endif

int handle_print(msg_t * msg, module_info * mod);
int handle_query(msg_t * msg, module_info * mod);
int handle_run(msg_t * msg, module_info * mod);

void start_all(bool automation);
void do_run_all(void *m);
int reconfig(const char *cfg);
layout *acquire_cur_layout();
void release_cur_layout();
layout *switch_cur_layout_locked(layout * lay, module_info * mod);
void set_main_module(module_info * mod);
module_info *get_main_module();
void flush_result();
void set_boot_mode(boot_mode_type mode);
void switch_home_screen(bool is_next);
bool is_cur_home_screen();

layout *get_main_layout();
bool is_main_screen();
layout *get_layout(module_info * mod);
layout *get_layout(string layout_name);
bool init_draw();
void set_font_size(const char *font_size);
void invalidate();
int get_fb_width();
int get_fb_height();
float get_pixels_percent_x();
float get_pixels_percent_y();
void enter_shipmode_thread();
#endif                          /* __MMI_H */
