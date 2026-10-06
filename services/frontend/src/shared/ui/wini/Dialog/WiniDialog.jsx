import { useMemo, useEffect, forwardRef } from 'react';
import Dialog from '@mui/material/Dialog';
import { cn } from '@/shared/lib/cn';
import { getUiTokens, hasUiToken, mergeUiSlotSxByTokens } from '@/shared/config/theme/uiTokens';
import { color } from '@/shared/config/theme';
import { size } from '@/shared/config/theme';

const baseSx = {
    
    // ui : 기본
    '& .MuiDialog-paper': {
        backgroundColor: color.background.white,
        borderTop : `2px solid ${color.background.main}`,
    },
    '& .MuiDialogTitle-root': {
        color: color.text.main,
    },
    '& .MuiDialogTitle-root:before': {
        content: '""',
        position: 'absolute',
        width: 'calc(100% - 16px)',
        height: '1px',
        display: 'block',
        bottom: 0,
        left: '50%',
        transform: 'translateX(-50%)',
        backgroundColor: color.background.divider,
    },

    '& .MuiDialogContainer': {
        minHeight: '8.4rem',
        padding: size.margin.md,
        paddingTop: `${size.margin.md} !important`,
        backgroundColor: color.background.white,
    },

    // ui : 기본, color : caution
    '&.winidialog--caution .MuiDialog-paper': {
        borderTop : `2px solid ${color.state.caution}`,
    },
    '&.winidialog--caution .MuiDialogTitle-root': {
        color: color.state.caution,
    },

    // ui : 기본, color : error
    '&.winidialog--error .MuiDialog-paper': {
        borderTop : `2px solid ${color.state.error}`,
    },
    '&.winidialog--error .MuiDialogTitle-root': {
        color: color.state.error,
    },
};

const UI_SX = {
    btn: {
        root: {
            // ui : 버튼형, color : ""
            '&.winidialog--btn .MuiDialog-paper': {
                padding: size.margin.sm,
                backgroundColor: color.gray.c_eee,
                borderTop: 'none',
                borderRadius: size.radius.md,
            },

            '&.winidialog--btn .MuiDialogTitle-root:before': {
                display: 'none',
            },

            '&.winidialog--btn .winibuttongroup' : {
                borderTopLeftRadius: 4,
                borderTopRightRadius: 4,
                overflow: 'hidden',
            },
            
            '& .winidialog__footer.winibuttongroup': {
                borderTopWidth: 1,
                borderTopStyle: 'solid',
                borderTopColor: color.background.divider,

                '& .winibutton': {
                    color: color.text.default,
                    fontSize: 15,
                    fontWeight: '600',
                    borderRadius: 0,

                    '& + .winibutton': {
                        borderLeftWidth: 1,
                        borderLeftStyle: 'solid',
                        borderLeftColor: color.background.divider,
                    },

                    '&.MuiButton-textMaindark': {
                        color: color.text.main,
                    },
                },
            },
        },
    }
};

const WiniDialog = forwardRef(
    (
        {
            children,
            color = 'normal',
            ui,
            layout: legacyLayout,
            btns,
            className,
            PaperProps: paperProps = {},
            variant,
            sx: dialogSxProp = {},
            onClose,
            ...props
        },
        ref,
    ) => {
        const hasMuiVariant = variant !== undefined && variant !== null;
        const resolvedColor = hasMuiVariant ? undefined : color ?? 'normal';
        const uiTokens = hasMuiVariant ? [] : getUiTokens(ui, legacyLayout ?? '');

        const mergedClassName = useMemo(() => cn(
            'winicomponent winidialog',
            resolvedColor && `winidialog--${resolvedColor}`,
            uiTokens.map((token) => `winidialog--${token}`),
            uiTokens.map((token) => `winidialog--layout-${token}`),
            className,
        ), [resolvedColor, uiTokens, className]);

        useEffect(() => {}, [props.open, onClose]);

        const { sx: paperSxProp, ...restPaperProps } = paperProps ?? {};
        const basePaperSx = hasMuiVariant
            ? {}
            : {
                ...(resolvedColor ? { boxShadow: 'none' } : {}),
            };
        const uiRootSx = mergeUiSlotSxByTokens(uiTokens, UI_SX, 'root');
        const uiPaperSx = mergeUiSlotSxByTokens(uiTokens, UI_SX, 'paper');

        return (
            <Dialog
                {...props}
                ref={ref}
                variant={variant}
                className={mergedClassName}
                onClose={onClose}
                sx={(theme) => ({
                    ...baseSx,
                    ...(uiRootSx ?? {}),
                    ...(typeof dialogSxProp === 'function' ? dialogSxProp(theme) : dialogSxProp),
                })}
                PaperProps={{
                    ...restPaperProps,
                    sx: (theme) => ({
                        ...basePaperSx,
                        // backgroundColor: color.background.white,
                        // padding: size.margin.sm,
                        borderRadius:0,
                        borderBottomLeftRadius: size.radius.md,
                        borderBottomRightRadius: size.radius.md,
                        ...(uiPaperSx ?? {}),
                        ...(typeof paperSxProp === 'function' ? paperSxProp(theme) : paperSxProp),
                    }),
                }}
            >
                {children}

                {hasUiToken(uiTokens, 'btn') && btns ? (
                    <div className="winidialog__footer winibuttongroup">{btns}</div>
                ) : null}
            </Dialog>
        );
    },
);

export default WiniDialog;
