#!/bin/bash

{
    grep "\] (WW)" /var/log/Xorg.0.log | sed 's/.*] (WW) /Warning: /I'

    grep "\] (II)" /var/log/Xorg.0.log | sed 's/.*] (II) /Information: /I'
} > X_info_warn.log

cat X_info_warn.log