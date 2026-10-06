import { forwardRef } from 'react';
import DialogTitle from '@mui/material/DialogTitle';
import { color } from '@/shared/config/theme';
import { size } from '@/shared/config/theme';

const WiniDialogTitle = forwardRef(

    ({ children, purpose = "alert", ...props }, ref) => {
    let className = "winicomponent winidialogtitle ";
    if(props.className!==undefined && props.className!==null){
        className += props.className
    }
        
    const isEmergency = purpose === "emergency";
        
    return (
        <DialogTitle
            {...props}
            ref={ref}
            className={className}
            sx={(theme) => ({

                '&.MuiDialogTitle-root': {
                    position: 'relative',
                    minHeight: size.objH.sm,
                    padding: size.margin.md,
                    color: color.text.main,
                    fontSize: size.text.md,
                    fontWeight: 500,
                    lineHeight: size.lh.none,
                    borderRadius: `${size.radius.xsm} ${size.radius.xsm} 0 0`,
                },
                '.winidialog--btn &.MuiDialogTitle-root': {
                    color: color.text.white,
                    borderRadius: 0,
                    borderTopLeftRadius: 4,
                    borderTopRightRadius: 4,
                    backgroundColor : color.background.main,
                },
    
                '&.MuiDialogTitle-root .MuiButton-DialogClose': {
                    position: 'absolute',
                    right: 6,
                    top: '50%',
                    transform: 'translateY(-50%)',
                    width:20,
                    height: 20,
                    borderRadius: 0,
                    padding: 0,
                    cursor: 'pointer',
                },
                '&.MuiDialogTitle-root .MuiButton-DialogClose svg': {
                    width: '100%',
                    height: '100%',
                    color: color.text.main,
                },
                // ui : 기본, color : caution 아이콘 색상
                '.winidialog--caution &.MuiDialogTitle-root .MuiButton-DialogClose svg': {
                    color: color.state.caution,
                },

                // ui : 기본, color : error 아이콘 색상
                '.winidialog--error &.MuiDialogTitle-root .MuiButton-DialogClose svg': {
                    color: color.state.error,
                },

                // ui : 버튼형, 기본 아이콘 색상
                '.winidialog--btn &.MuiDialogTitle-root .MuiButton-DialogClose svg': {
                    color: color.text.white,
                },

                // ui : 버튼형, color : error 타이틀 배경색
                '.winidialog--btn.winidialog--error &.MuiDialogTitle-root': {
                    color: color.text.white,
                    backgroundColor: color.state.error,
                },

                // ui : 버튼형, color : caution 타이틀 배경색
                '.winidialog--btn.winidialog--caution &.MuiDialogTitle-root': {
                    color: color.text.white,
                    backgroundColor: color.state.caution,
                },
            })}
        >
            {children}
        </DialogTitle>
    );
})
export default WiniDialogTitle
