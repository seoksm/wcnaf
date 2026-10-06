import { forwardRef } from 'react';
import DialogActions from '@mui/material/DialogActions';
const WiniDialogActions = forwardRef(({ children, ...props }, ref) => {
    let className = "winicomponent winidialogactions ";
    if(props.className!==undefined && props.className!==null){
        className += props.className
    }
    return (
        <DialogActions {...props} className ={className} ref = {ref} >
            {children}
        </DialogActions>
    );
})
export default WiniDialogActions