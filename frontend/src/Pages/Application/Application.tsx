import ImageUpload from "../../Components/ImageUpload/ImageUpload";
import Sidebar from "../../Components/Sidebar/Sidebar";

const Application = () => {
    return (
        <div className="flex">
            <Sidebar />
            <div className="flex-1">
                <ImageUpload />
            </div>
        </div>
    );
};

export default Application;