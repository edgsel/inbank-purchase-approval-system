export const getStatusLabel = (val) => {
    if (val === true) return 'Approved';
    if (val === false) return 'Rejected';
    return 'Validation Error';
};
