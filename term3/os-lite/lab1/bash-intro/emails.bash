#!/bin/bash

emails=$(grep -PRoh "[a-zA-Z.+-]+@[a-zA-Z-]+\.[a-zA-Z.-]+" /etc 2>/dev/null | sort -u)

echo "$emails" | paste -sd, - | sed 's/$/,/' > etc_emails.lst