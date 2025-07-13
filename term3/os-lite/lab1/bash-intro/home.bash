#!/bin/bash

[ "$PWD" = "$HOME" ] && echo "Home directory: $HOME" && exit 0 || echo "Error: Script is not running from the home directory" && exit 1
