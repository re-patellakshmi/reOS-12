# Copyright (C) 2008 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

.source "T_add_double_6.java"
.class  public Ldot/junit/opcodes/add_double/d/T_add_double_6;
.super  Ljava/lang/Object;


.method public constructor <init>()V
.registers 1
       invoke-direct {v0}, Ljava/lang/Object;-><init>()V
       return-void
.end method

.method public run(II)V
.registers 7
       move v0, v5
       move v1, v5
       move v2, v6    
       move v3, v6    
       add-double v0, v0, v2
       return-void
.end method


