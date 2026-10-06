import React, { useState } from 'react';
import {
    WiniBox,
    WiniButton,
    WiniCollapse,
    WiniIcon,
    WiniList,
    WiniListItem,
    WiniListItemText,
    WiniTypography,
} from '@/shared/ui/wini';
import { CATEGORY_ORDER, GROUPED_COMPONENTS } from '../model/paletteData';

export const Palette = ({ onSelectTemplate }) => {
    const [openSections, setOpenSections] = useState({
        layout: true,
        form: true,
        action: true,
        etc: true,
    });

    const renderPaletteItem = (item) => (
        <WiniBox key={item.templateId} className="mb-0.5">
            <WiniButton
                onClick={() => onSelectTemplate(item)}
                ui="white"
                className="w-full justify-start text-left min-h-0 cursor-pointer p-2"
            >
                <WiniBox className="w-full">
                    <WiniTypography variant="span" className="font-semibold block">
                        {item.label}
                    </WiniTypography>
                </WiniBox>
            </WiniButton>
        </WiniBox>
    );

    const renderPaletteSection = (categoryKey) => {
        const group = GROUPED_COMPONENTS.find((item) => item.id === categoryKey);
        if (!group) return null;

        const items = group.items || [];
        const isOpen = openSections[categoryKey];

        return (
            <WiniListItem key={categoryKey}>
                <WiniListItemText>
                    <WiniButton
                        className="depth1"
                        aria-expanded={isOpen}
                        aria-controls={`palette-${categoryKey}`}
                        onClick={() =>
                            setOpenSections((prev) => ({
                                ...prev,
                                [categoryKey]: !prev[categoryKey],
                            }))
                        }
                    >
                        {group.title} ({items.length})
                        <WiniIcon icon={isOpen ? 'up' : 'down'} />
                    </WiniButton>

                    <WiniCollapse in={isOpen} mountOnEnter unmountOnExit>
                        <WiniList id={`palette-${categoryKey}`} ui="dep_02" className="mt-1">
                            {items.map((item) => renderPaletteItem(item))}
                        </WiniList>
                    </WiniCollapse>
                </WiniListItemText>
            </WiniListItem>
        );
    };

    return (
        <WiniBox className="border border-[#e4e4e4] p-[14px] bg-[#fafafa] h-full min-h-0 flex flex-col">
            <WiniTypography variant="h5" className="my-2">
                컴포넌트
            </WiniTypography>

            <WiniBox className="flex-1 min-h-0 overflow-y-auto pr-1">
                {CATEGORY_ORDER.map((categoryKey) => renderPaletteSection(categoryKey))}
            </WiniBox>
        </WiniBox>
    );
};