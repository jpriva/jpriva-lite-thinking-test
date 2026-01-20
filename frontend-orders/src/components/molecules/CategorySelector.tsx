import {MenuItem, type TextFieldProps} from "@mui/material";
import {FormInput} from "../atoms";
import type {Category} from "../../types";
import type {ChangeEvent} from "react";

type CategorySelectorProps = Omit<TextFieldProps, 'onChange'> & {
    value: string;
    onChange: (event: ChangeEvent<HTMLInputElement>) => void;
    categories: Category[];
    error?: boolean;
};
export const CategorySelector = ({value, onChange, categories, error, ...props}: CategorySelectorProps) => {
    return (
        <FormInput
            select
            label={props.label || "Category"}
            value={value}
            error={error}
            onChange={onChange}
            helperText={props.helperText || "Select the category for this product"}
            {...props}
        >
            {categories.length > 0 ? (
                categories.map((cat) => (
                    <MenuItem key={cat.id} value={cat.id}>
                        {cat.name}
                    </MenuItem>
                ))
            ) : (
                <MenuItem value="" disabled>
                    No categories found. Create one first!
                </MenuItem>
            )}
        </FormInput>
    );
};