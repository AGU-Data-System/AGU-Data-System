import * as React from 'react'
import ReturnButton from "../Layouts/ReturnButton";

export default function AguHeader(
    {aguOrd, aguName, contacts, isFavorite} : {aguOrd: string, aguName: string, contacts: string[], isFavorite: boolean}
){
    return(
        <div>
            {aguOrd + aguName + contacts + isFavorite}
            <ReturnButton />
        </div>
    )
}