import React from "react";
import Header from "../Components/Header";
import {Outlet} from "react-router-dom";

const Main = () => {
    return(
        <>
            <Header/>
            <div>
                Main
            </div>
        </>
    );
};

export default Main;