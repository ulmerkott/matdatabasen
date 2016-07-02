#!/bin/bash
# Script to update a the livsmedel DB with information about portion sizes from the fineli.fi website.

DATABASE_FILE=LivsmedelsDB_fineli.sqlite

# Use the following to resume from a specific iteration if previous scraping failed because of a hanging wget call.
RESUME_FROM=0

SCRAPE_URL_BASE="https://fineli.fi/fineli/sv/elintarvikkeet/"
SCRAPE_URL_END="?foodType=ANY&portionUnit=G&portionSize=100&sortByColumn=name&sortOrder=asc&component=2331&"

IFS=$'\n'
size=`echo 'SELECT COUNT(id) FROM livsmedel;' | sqlite3 $DATABASE_FILE`
count=1
for id in `echo 'SELECT id FROM livsmedel;' | sqlite3 $DATABASE_FILE`; do
    if [ $count -lt $RESUME_FROM ]; then
         let count=count+1
         continue
    fi

    atName=true
    separator=""
    portionString=""
    for line in `wget -q -O - "$SCRAPE_URL_BASE$id$SCRAPE_URL_END" |grep Portionsstorlek -A 100 |grep portionSize -A 2 |grep -v "href\| 100 g\|a>\|\-\-" | sed -e 's/^[[:space:]]*//'`; do
        if [[ $atName = true ]]; then
            #echo NAME: $line
            name=$line
            atName=false
        else
            #echo VALUE: $line
            value=$line
            atName=true
            portionString="$portionString$separator$name:$value"
            separator=","
        fi
    done
    if [ -n $portionString ]; then
        echo "UPDATE livsmedel SET portions = \"$portionString\" WHERE id LIKE \"$id\";" | sqlite3 $DATABASE_FILE
    else
        echo "Portionstring was empty..."
        exit 1
    fi
    echo "SCRAPING iteration $count out of $size."
    let count=count+1
done


